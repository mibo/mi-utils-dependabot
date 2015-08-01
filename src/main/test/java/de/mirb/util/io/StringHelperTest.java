package de.mirb.util.io;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by michael on 01.08.15.
 */
public class StringHelperTest {

  @Test
  public void testContains() throws Exception {
    String testcaseOne = "This is a simple test string.";

    assertTrue(StringHelper.contains(testcaseOne, "is", "simple").isSuccess());
    assertTrue(StringHelper.contains(testcaseOne, "is a", "simple").isSuccess());
    assertFalse(StringHelper.contains(testcaseOne, "is", "complex").isSuccess());
    assertFalse(StringHelper.contains(testcaseOne, "is", "test", "a", "simple").isSuccess());
  }
}