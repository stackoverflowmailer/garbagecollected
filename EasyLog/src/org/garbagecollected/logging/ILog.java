/*
 * Copyright (C) 2008 Robbie Vanbrabant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.garbagecollected.logging;

/** 
 * Simple logging.
 */
public interface ILog {
  /** Publishes a message to the given log level. */
  void publish(Level level, Object... msg);
  
  /** Enable all log levels. */
  void enableAllLevels();
  /** Disable all log levels. */
  void disableAllLevels();
  /** Enable the given log level. */
  void enable(Level level);
  /** Disable the given log level. */
  void disable(Level level);
  
  
  public enum Level {
    LITTLE, NORMAL, MUCH, VERY_MUCH, INSANE
  }
}