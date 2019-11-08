package com.ticup.business.generate.create;

/**
 * TODO
 *
 * @author chenhao
 * @version 1.0.0
 * @since 1.0.0
 *
 * Created at 2019-11-06 10:46
 */
public class GenerateUtil {

  public static String upper(String target) {
    String start = target.substring(0, 1).toUpperCase();
    String after = target.substring(1);
    return start + after;
  }

}
