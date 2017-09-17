package com.alexanderageychenko.ecometer;

import com.alexanderageychenko.ecometer.Model.Depository.MetersDepository;
import com.alexanderageychenko.ecometer.Model.Entity.IMeter;
import com.alexanderageychenko.ecometer.Octopus.details.DetailsOctopus;
import com.alexanderageychenko.ecometer.Octopus.home.HomeOctopus;
import com.alexanderageychenko.ecometer.Tools.DefaultMetersFiller;
import com.alexanderageychenko.ecometer.tools.TestTools;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.reactivex.Observable;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Alexander on 01.05.2017.
 */

@RunWith(value = BlockJUnit4ClassRunner.class)
public class HomeOctopusTest extends TestRoot {
    @Mock
    MetersDepository metersDepository;
    @InjectMocks
    HomeOctopus homeOctopus;
    @InjectMocks
    DetailsOctopus detailsOctopus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(timeout = 10000)
    public void setMetersList() {

        stop = new Boolean[]{false, false};
        DefaultMetersFiller filer = new DefaultMetersFiller();
        final ArrayList<IMeter> defaultMeters = new ArrayList<>(filer.getDefaultMeters());

        when(metersDepository.getSelectedMeter())
                .thenReturn(defaultMeters.get(0));

        when(metersDepository.getMetersPublisher())
                .thenReturn(Observable.just(defaultMeters)
                        .map(iMeters -> iMeters));

        homeOctopus.getMetersObservable().subscribe(iMeters -> {
            if (iMeters.isEmpty()) return;
            Assert.assertEquals(new ArrayList<>(iMeters), defaultMeters);
            stop[0] = true;
        });

        detailsOctopus.getMeterFullnameObservable().subscribe(fullname -> {
            if (fullname.isEmpty()) return;
            Assert.assertEquals(fullname, defaultMeters.get(0).getFullName());
            stop[1] = true;
        });

        homeOctopus.onStart();
        detailsOctopus.onStart();

        TestTools.pause(stop);
        Assert.assertFalse(fail[0]);

        homeOctopus.onStop();
        detailsOctopus.onStop();

        verify(metersDepository, atLeastOnce()).getMetersPublisher();
    }
}