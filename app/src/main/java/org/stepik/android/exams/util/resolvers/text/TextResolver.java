package org.stepik.android.exams.util.resolvers.text;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TextResolver {

    @NotNull
    TextResult resolveStepText(String content);

    /**
     * For SIMPLE HTML, which you can set for textview only
     */
    @NotNull
    CharSequence fromHtml(@Nullable String content);

    @NotNull
    String replaceWhitespaceToBr(@Nullable String answer);

}