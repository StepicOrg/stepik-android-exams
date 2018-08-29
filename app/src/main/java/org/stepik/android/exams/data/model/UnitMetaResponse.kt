package org.stepik.android.exams.data.model

import org.stepik.android.model.Meta
import org.stepik.android.model.Unit

class UnitMetaResponse(meta: Meta, val units: List<Unit>?) : MetaResponseBase(meta)