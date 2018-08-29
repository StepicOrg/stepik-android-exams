package org.stepik.android.exams.data.model

import org.stepik.android.model.Meta
import org.stepik.android.model.Step


class StepResponse(meta: Meta, val steps: List<Step>? = null) : MetaResponseBase(meta)
