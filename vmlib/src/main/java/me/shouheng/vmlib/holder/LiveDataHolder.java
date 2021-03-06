package me.shouheng.vmlib.holder;

import android.arch.lifecycle.MutableLiveData;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import me.shouheng.vmlib.base.SingleLiveEvent;
import me.shouheng.vmlib.bean.Resources;

/**
 * One holder for {@link android.arch.lifecycle.LiveData}
 *
 * @author <a href="mailto:shouheng2015@gmail.com">WngShhng</a>
 * @version 2020-05-17 20:34
 */
public class LiveDataHolder<T> {

    private Map<Class, SingleLiveEvent> map = new HashMap<>();

    private Map<Class, SparseArray<SingleLiveEvent>> flagMap = new HashMap<>();

    public MutableLiveData<Resources<T>> getLiveData(Class<T> dataType, boolean single) {
        SingleLiveEvent<Resources<T>> liveData = map.get(dataType);
        if (liveData == null) {
            liveData = new SingleLiveEvent<>(single);
            map.put(dataType, liveData);
        }
        return liveData;
    }

    public MutableLiveData<Resources<T>> getLiveData(Class<T> dataType, int flag, boolean single) {
        SparseArray<SingleLiveEvent> array = flagMap.get(dataType);
        if (array == null) {
            array = new SparseArray<>();
            flagMap.put(dataType, array);
        }
        SingleLiveEvent<Resources<T>> liveData = array.get(flag);
        if (liveData == null) {
            liveData = new SingleLiveEvent<>(single);
            array.put(flag, liveData);
        }
        return liveData;
    }
}
