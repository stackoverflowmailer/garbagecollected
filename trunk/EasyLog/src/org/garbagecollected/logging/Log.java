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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class Log implements ILog {
  /** Keeps track of which {@link Level}s are enabled. */
  private BitSet enabled;
  
  /** Specifies the format of a single line. */
  private final Format format;
  
  /** The {@link OutputStream} to write logging to. */
  private final OutputStream os;

  /**
   * Create a new Log that will use the given log format.
   * This controls which calls to 
   * {@link #publish(org.garbagecollected.logging.Log.Level, Object...)} are valid.
   * Using this constructor the Log will write to System.out.
   * 
   * @param format how each logged line should look
   */
  public Log(Format format) {
    this(format, System.out);
  }
  
  /**
   * Create a new Log that will use the given log format.
   * This controls which calls to 
   * {@link #publish(org.garbagecollected.logging.Log.Level, Object...)} are valid.
   * 
   * @param format how each logged line should look
   * @param os the {@link OutputStream} to which log information will be written
   */
  public Log(Format format, OutputStream os) {
    precondition(format != null, "Format may not be null");
    precondition(os != null, "OutputStream may not be null");
    this.format = format;
    this.os = os;
    disableAllLevels();
  }

  /**
   * @see org.garbagecollected.logging.ILog#publish(org.garbagecollected.logging.Log.Level, java.lang.Object)
   */
  public void publish(Level level, Object... msg) {
    if (enabled.get(level.ordinal()-1)) {
      byte[] line;
      try {
        line = format.constructLine(level, msg).getBytes("UTF-8");
      } catch (UnsupportedEncodingException e1) {
        throw new RuntimeException(e1);
      }
      try {
        this.os.write(line, 0, line.length);
        this.os.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * @see org.garbagecollected.logging.ILog#enableAllLevels()
   */
  public void enableAllLevels() {
    enabled.set(0, Level.values().length);
  }
  
  /**
   * @see org.garbagecollected.logging.ILog#enable(org.garbagecollected.logging.Log.Level)
   */
  public void enable(Level level) {
    enabled.set(0, level.ordinal());
  }

  /**
   * @see org.garbagecollected.logging.ILog#disableAllLevels()
   */
  public void disableAllLevels() {
    enabled = new BitSet();
  }
  
  /**
   * @see org.garbagecollected.logging.ILog#disable(org.garbagecollected.logging.Log.Level)
   */
  public void disable(Level level) {
    enabled.set(0, level.ordinal(), false);
  }
  
  private static void precondition(boolean condition, String msg) {
    if (!condition) throw new IllegalArgumentException(msg);
  }

  public enum FormatOption {
    /** Log Level. */
    LEVEL, 
    /** Thread ID. */
    THREAD_ID,
    /** Placeholder for a message provided at runtime. */
    RUNTIME_PARAMETER
  }

  public static class Format {
    private final String format;
    private final FormatOption[] lineStructure;
    public Format(String format, FormatOption... lineStructure) {
      precondition(format != null, "format may not be null");
      precondition(lineStructure.length>0, "Need at least one FormatOption");
      this.format = format + "%n"; // platform independent line break
      this.lineStructure = new FormatOption[lineStructure.length];
      // defensive copy
      System.arraycopy(lineStructure, 0, this.lineStructure, 0, lineStructure.length);
    }

    public String constructLine(Level level, Object[] provided) {
      Object[] formatArguments = new Object[lineStructure.length];
      
      for (int i = 0, index = 0; i < lineStructure.length; i++) {
        if (FormatOption.LEVEL == lineStructure[i]) {
          formatArguments[i] = level;
        } else if (FormatOption.THREAD_ID == lineStructure[i]) {
          formatArguments[i] = Thread.currentThread().getId();
        } else if (FormatOption.RUNTIME_PARAMETER == lineStructure[i]) {
          int position = index;
          if (position >= provided.length) {
            throw new IllegalArgumentException("Expecting more arguments than "+provided.length);
          }
          formatArguments[i] = provided[position];
          index++;
        } else {
          throw new IllegalArgumentException("Unknown FormatOption: "+lineStructure[i]);
        }
      }
      return String.format(format, formatArguments);
    }
  }
}
