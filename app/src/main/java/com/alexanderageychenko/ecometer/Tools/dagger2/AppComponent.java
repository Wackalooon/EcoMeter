package com.alexanderageychenko.ecometer.Tools.dagger2;

import android.content.Context;
import android.support.annotation.Nullable;

import com.alexanderageychenko.ecometer.Tools.dagger2.Module.AppModule;
import com.alexanderageychenko.ecometer.Tools.dagger2.Module.DepositoryModule;
import com.alexanderageychenko.ecometer.Tools.dagger2.Module.OctopusModule;
import com.alexanderageychenko.ecometer.Tools.dagger2.Module.UIModule;
import com.alexanderageychenko.ecometer.Tools.dagger2.SubComponent.Getter;
import com.alexanderageychenko.ecometer.Tools.dagger2.SubComponent.Injector;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Alexander on 15.04.2017.
 */
@Singleton
@Component(modules={DepositoryModule.class, AppModule.class, OctopusModule.class, UIModule.class})
public interface AppComponent {
    Getter getGetter();
    Injector getInjector();

    @Nullable
    Context getContext();
}