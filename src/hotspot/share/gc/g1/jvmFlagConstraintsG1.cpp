/*
 * Copyright (c) 2015, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#include "gc/g1/g1HeapRegionBounds.inline.hpp"
#include "gc/g1/g1HeapSizingPolicy.hpp"
#include "gc/g1/jvmFlagConstraintsG1.hpp"
#include "gc/shared/bufferNode.hpp"
#include "gc/shared/ptrQueue.hpp"
#include "runtime/globals_extension.hpp"
#include "utilities/globalDefinitions.hpp"

JVMFlag::Error G1RemSetArrayOfCardsEntriesConstraintFunc(uint value, bool verbose) {
  if (!UseG1GC) return JVMFlag::SUCCESS;

  // Default value of G1RemSetArrayOfCardsEntries=0 means will be set ergonomically.
  // Minimum value is 1.
  if (FLAG_IS_CMDLINE(G1RemSetArrayOfCardsEntries) && (value < 1)) {
    JVMFlag::printError(verbose,
                        "G1RemSetArrayOfCardsEntries (%u) must be "
                        "greater than or equal to 1.\n",
                        value);
    return JVMFlag::VIOLATES_CONSTRAINT;
  } else {
    return JVMFlag::SUCCESS;
  }
}

JVMFlag::Error G1RemSetHowlNumBucketsConstraintFunc(uint value, bool verbose) {
  if (!UseG1GC) return JVMFlag::SUCCESS;

  if (!FLAG_IS_CMDLINE(G1RemSetHowlNumBuckets)) {
    return JVMFlag::SUCCESS;
  }
  if (value == 0 || !is_power_of_2(G1RemSetHowlNumBuckets)) {
    JVMFlag::printError(verbose,
                        "G1RemSetHowlNumBuckets (%u) must be a power of two "
                        "and greater than or equal to 1.\n",
                        value);
    return JVMFlag::VIOLATES_CONSTRAINT;
  }
  return JVMFlag::SUCCESS;
}

JVMFlag::Error G1RemSetHowlMaxNumBucketsConstraintFunc(uint value, bool verbose) {
  if (!UseG1GC) return JVMFlag::SUCCESS;

  if (!FLAG_IS_CMDLINE(G1RemSetHowlMaxNumBuckets)) {
    return JVMFlag::SUCCESS;
  }
  if (!is_power_of_2(G1RemSetHowlMaxNumBuckets)) {
    JVMFlag::printError(verbose,
                        "G1RemSetMaxHowlNumBuckets (%u) must be a power of two.\n",
                        value);
    return JVMFlag::VIOLATES_CONSTRAINT;
  }
  return JVMFlag::SUCCESS;
}

JVMFlag::Error G1HeapRegionSizeConstraintFunc(size_t value, bool verbose) {
  if (!UseG1GC) return JVMFlag::SUCCESS;

  // Default value of G1HeapRegionSize=0 means will be set ergonomically.
  if (FLAG_IS_CMDLINE(G1HeapRegionSize) && (value < G1HeapRegionBounds::min_size())) {
    JVMFlag::printError(verbose,
                        "G1HeapRegionSize (%zu) must be "
                        "greater than or equal to ergonomic heap region minimum size\n",
                        value);
    return JVMFlag::VIOLATES_CONSTRAINT;
  } else {
    return JVMFlag::SUCCESS;
  }
}

JVMFlag::Error G1NewSizePercentConstraintFunc(uint value, bool verbose) {
  if (!UseG1GC) return JVMFlag::SUCCESS;

  if (value > G1MaxNewSizePercent) {
    JVMFlag::printError(verbose,
                        "G1NewSizePercent (%u) must be "
                        "less than or equal to G1MaxNewSizePercent (%u)\n",
                        value, G1MaxNewSizePercent);
    return JVMFlag::VIOLATES_CONSTRAINT;
  } else {
    return JVMFlag::SUCCESS;
  }
}

JVMFlag::Error G1MaxNewSizePercentConstraintFunc(uint value, bool verbose) {
  if (!UseG1GC) return JVMFlag::SUCCESS;

  if (value < G1NewSizePercent) {
    JVMFlag::printError(verbose,
                        "G1MaxNewSizePercent (%u) must be "
                        "greater than or equal to G1NewSizePercent (%u)\n",
                        value, G1NewSizePercent);
    return JVMFlag::VIOLATES_CONSTRAINT;
  } else {
    return JVMFlag::SUCCESS;
  }
}

JVMFlag::Error MaxGCPauseMillisConstraintFuncG1(uintx value, bool verbose) {
  if (UseG1GC && FLAG_IS_CMDLINE(MaxGCPauseMillis) && (value >= GCPauseIntervalMillis)) {
    JVMFlag::printError(verbose,
                        "MaxGCPauseMillis (%zu) must be "
                        "less than GCPauseIntervalMillis (%zu)\n",
                        value, GCPauseIntervalMillis);
    return JVMFlag::VIOLATES_CONSTRAINT;
  }

  return JVMFlag::SUCCESS;
}

JVMFlag::Error GCPauseIntervalMillisConstraintFuncG1(uintx value, bool verbose) {
  if (UseG1GC) {
    if (FLAG_IS_CMDLINE(GCPauseIntervalMillis)) {
      if (value < 1) {
        JVMFlag::printError(verbose,
                            "GCPauseIntervalMillis (%zu) must be "
                            "greater than or equal to 1\n",
                            value);
        return JVMFlag::VIOLATES_CONSTRAINT;
      }

      if (FLAG_IS_DEFAULT(MaxGCPauseMillis)) {
        JVMFlag::printError(verbose,
                            "GCPauseIntervalMillis cannot be set "
                            "without setting MaxGCPauseMillis\n");
        return JVMFlag::VIOLATES_CONSTRAINT;
      }

      if (value <= MaxGCPauseMillis) {
        JVMFlag::printError(verbose,
                            "GCPauseIntervalMillis (%zu) must be "
                            "greater than MaxGCPauseMillis (%zu)\n",
                            value, MaxGCPauseMillis);
        return JVMFlag::VIOLATES_CONSTRAINT;
      }
    }
  }

  return JVMFlag::SUCCESS;
}

JVMFlag::Error NewSizeConstraintFuncG1(size_t value, bool verbose) {
#ifdef _LP64
  // Overflow would happen for uint type variable of YoungGenSizer::_min_desired_young_length
  // when the value to be assigned exceeds uint range.
  // i.e. result of '(uint)(NewSize / region size(1~32MB))'
  // So maximum of NewSize should be 'max_juint * 1M'
  if (UseG1GC && (value > (max_juint * 1 * M))) {
    JVMFlag::printError(verbose,
                        "NewSize (%zu) must be less than ergonomic maximum value\n",
                        value);
    return JVMFlag::VIOLATES_CONSTRAINT;
  }
#endif // _LP64
  return JVMFlag::SUCCESS;
}

size_t MaxSizeForHeapAlignmentG1() {
  return G1HeapRegionBounds::max_size();
}

static JVMFlag::Error buffer_size_constraint_helper(JVMFlagsEnum flagid,
                                                    size_t value,
                                                    bool verbose) {
  if (UseG1GC) {
    const size_t min_size = 1;
    const size_t max_size = BufferNode::max_size();
    JVMFlag* flag = JVMFlag::flag_from_enum(flagid);
    if ((value < min_size) || (value > max_size)) {
      JVMFlag::printError(verbose,
                          "%s (%zu) must be in range [%zu, %zu]\n",
                          flag->name(), value, min_size, max_size);
      return JVMFlag::OUT_OF_BOUNDS;
    }
  }
  return JVMFlag::SUCCESS;
}

JVMFlag::Error G1SATBBufferSizeConstraintFunc(size_t value, bool verbose) {
  return buffer_size_constraint_helper(FLAG_MEMBER_ENUM(G1SATBBufferSize),
                                       value,
                                       verbose);
}

JVMFlag::Error G1UpdateBufferSizeConstraintFunc(size_t value, bool verbose) {
  return buffer_size_constraint_helper(FLAG_MEMBER_ENUM(G1UpdateBufferSize),
                                       value,
                                       verbose);
}

JVMFlag::Error gc_cpu_usage_threshold_helper(JVMFlagsEnum flagid,
                                             uint value,
                                             bool verbose) {
  if (UseG1GC) {
    JVMFlag* flag = JVMFlag::flag_from_enum(flagid);
    const uint min_count = 1;
    const uint max_count = G1HeapSizingPolicy::long_term_count_limit();
    if (value < min_count || value > max_count) {
      JVMFlag::printError(verbose,
                          "%s (%u) must be in range [%u, %u]\n",
                          flag->name(), value, min_count, max_count);
      return JVMFlag::VIOLATES_CONSTRAINT;
    }
  }
  return JVMFlag::SUCCESS;
}

JVMFlag::Error G1CPUUsageExpandConstraintFunc(uint value, bool verbose) {
  return gc_cpu_usage_threshold_helper(FLAG_MEMBER_ENUM(G1CPUUsageExpandThreshold),
                                       value,
                                       verbose);
}

JVMFlag::Error G1CPUUsageShrinkConstraintFunc(uint value, bool verbose) {
  return gc_cpu_usage_threshold_helper(FLAG_MEMBER_ENUM(G1CPUUsageShrinkThreshold),
                                       value,
                                       verbose);
}
