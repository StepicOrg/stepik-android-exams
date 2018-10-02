package org.stepik.android.exams.util

import org.stepik.android.model.Step

fun Step?.getStepType(): String = this?.block?.name ?: AppConstants.TYPE_NULL