package oxxy.kero.roiaculte.team7.starterproject.Ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import oxxy.kero.roiaculte.team7.starterproject.Ui.feature.FeatureActivity
import oxxy.kero.roiaculte.team7.starterproject.Ui.feature.FeatureModule
import oxxy.kero.roiaculte.team7.starterproject.Ui.feature.fragment1.FragmentProvider
import oxxy.kero.roiaculte.team7.starterproject.di.viewmodel.ViewModelModule

/**
 * all Activitys will be injeted here by  @ContributesAndroidInjector with here modules
 */
@Module
abstract class ActivitysBuilder {
    @ContributesAndroidInjector(modules = [FeatureModule::class
        , FragmentProvider::class])
    abstract fun provideActivity(): FeatureActivity
}