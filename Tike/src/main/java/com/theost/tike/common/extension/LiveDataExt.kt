package com.theost.tike.common.extension

import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KProperty1

inline fun <reified T, reified K, I, X, Y> MutableLiveData<List<I>>.hideItem(
    field1: KProperty1<T, X>,
    filter1: X,
    field2: KProperty1<K, Y>,
    filter2: Y
) {
    value?.let { items ->
        items.filterNot { it is T && field1.get(it) == filter1 }.let { filtered ->
            postValue(
                if (filtered.filterIsInstance<T>().isEmpty()){
                    filtered.filterNot { it is K && field2.get(it) == filter2 }
                } else {
                    filtered
                }
            )
        }
    }
}

inline fun <reified T, I, X> MutableLiveData<List<I>>.hideItem(
    field: KProperty1<T, X>,
    filter: X
) {
    value?.let { items -> postValue(items.filterNot { it is T && field.get(it) == filter }) }
}

inline fun <reified T, I, X> MutableLiveData<List<I>?>.hideNullableItem(
    field: KProperty1<T, X>,
    filter: X
) {
    value?.let { items -> postValue(items.filterNot { it is T && field.get(it) == filter }) }
}
