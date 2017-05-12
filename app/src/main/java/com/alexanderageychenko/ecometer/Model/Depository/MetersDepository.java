package com.alexanderageychenko.ecometer.Model.Depository;

import com.alexanderageychenko.ecometer.Model.DataBase.IMetersDAO;
import com.alexanderageychenko.ecometer.Model.Entity.IMeter;
import com.alexanderageychenko.ecometer.Model.Entity.Meter;
import com.alexanderageychenko.ecometer.Tools.dagger2.Dagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by alexanderageychenko on 9/13/16.
 */

public class MetersDepository implements IMetersDepository {
    @Inject
    IMetersDAO metersDAO;
    private Long selectedMeterId;
    private HashMap<Long, IMeter> meters = new HashMap<>();
    private PublishSubject<Collection<IMeter>> metersPublisher = PublishSubject.create();


    public MetersDepository() {
        Dagger.get().getInjector().inject(this);
    }

    @Override
    public void setMeters(Collection<IMeter> iMeters) {
        meters.clear();
        for (IMeter m : iMeters)
            meters.put(m.getId(), m);

        saveToDB(iMeters);

    }

    private void saveToDB(Collection<IMeter> iMeters){
        Observable.just(iMeters) // use  to imitate rest request
                .observeOn(Schedulers.io())  //io for DB usage
                .subscribe(new Consumer<Collection<IMeter>>() {
                    @Override
                    public void accept(Collection<IMeter> iMeters) throws Exception {
                        metersDAO.set(iMeters);
                    }
                });
    }

    @Override
    public Collection<IMeter> getMeters() {
        if (meters.isEmpty()) {
//            DefaultMetersFiller filer = new DefaultMetersFiller();
//            ArrayList<Meter> defaultMeters = filer.getDefaultMeters();
//            for (IMeter m : defaultMeters)
//                meters.put(m.getId(), m);
        }
        return meters.values();
    }

    @Override
    public void addMeter(IMeter meter) {
        meters.put(meter.getId(), meter);
        saveMeters();
    }

    @Override
    public IMeter getMeter(Long id) {
        return meters.get(id);
    }

    @Override
    public Observable<Collection<IMeter>> getMetersPublisher() {
        return metersPublisher;
    }

    @Override
    public void requestMeters() {
        Observable.timer(1, TimeUnit.SECONDS) // use  to imitate rest request
                .observeOn(Schedulers.io())  //io for DB usage
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long time) throws Exception {

                        if (meters.isEmpty()) {//load meters from DAO
                            ArrayList<Meter> defaultMeters = metersDAO.get();
                            for (IMeter m : defaultMeters)
                                meters.put(m.getId(), m);
                        }
                        metersPublisher.onNext(meters.values());
                    }
                });

    }

    @Override
    public void selectMeter(Long meterId) {
        selectedMeterId = meterId;
    }

    @Override
    public IMeter getSelectedMeter() {
        return meters.get(selectedMeterId);
    }

    @Override
    public void saveMeters() {
        saveToDB(meters.values());
    }
}

