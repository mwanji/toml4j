package com.moandjiezana.toml;

public class CommentUtil {
  public static void addComments(final String[] comments, final WriterContext context) {
    addComments(comments, context, true);
  }

  public static void addComments(final String[] comments, final WriterContext context, boolean ident) {
    if (comments == null) return;
    for (final String comment : comments) {
      if (comment == null) continue;
      if (comment.contains("\n")) {
        final String[] split = comment.split("\n");
        for (final String c : split) {
          if (c == null) continue;
          if (ident) context.indent();
          context.write("# " + c + "\n");
        }
      } else {
        if (ident) context.indent();
        context.write("# " + comment + "\n");
      }
    }
  }
}
