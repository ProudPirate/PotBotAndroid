package com.plantdisease.plantdiseasedetection.base.adapter

import com.plantdisease.plantdiseasedetection.base.adapter.models.BaseModel


open class GenericListAdapter<T : BaseModel>
constructor(

) : BaseListAdapter<T, BindableViewHolder<T>>()
