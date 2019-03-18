package oxxy.kero.roiaculte.team7.domain.interactors

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import oxxy.kero.roiaculte.team7.domain.exception.Failure
import oxxy.kero.roiaculte.team7.domain.functional.AppRxSchedulers
import oxxy.kero.roiaculte.team7.domain.functional.Either

interface EitherInteractor<in P, out R , out F :Failure> {
    val dispatcher: CoroutineDispatcher
    val ResultDispatcher :CoroutineDispatcher
    suspend operator fun invoke(executeParams: P):Either<F, R>
}

interface Interactor<in P , out R >{
    val  dispatcher: CoroutineDispatcher
    val ResultDispatcher :CoroutineDispatcher
    suspend operator fun invoke(executeParams: P):R
}
abstract  class SubjectInteractor<Type  , in Params>(private val schedulers:AppRxSchedulers){
    private val subject = BehaviorSubject.create<Type>()
    private val compositeDisposeable = CompositeDisposable()
    protected abstract fun buildObservable(p:Params):Observable<Type>
    fun observe(p:Params ,  SuccesObserver:(t:Type)->Unit){
        buildObservable(p).subscribe(subject)
        compositeDisposeable.add( subject.subscribeOn(schedulers.computation)
            .observeOn(schedulers.main)
            .subscribe(SuccesObserver))
    }
    fun dispose(){
        compositeDisposeable.clear()
    }
}


abstract class ObservableCompleteInteractor<Type , in Params>(private val schedulers:AppRxSchedulers){

    protected abstract fun buildObservable(p:Params):Observable< Type>

    fun observe(p:Params, FailureObserver:(e:Throwable)->Unit , SuccesObserver:(t:Type)->Unit, JobCompletedObserver
    :()->Unit): Disposable {
        return buildObservable(p).subscribeOn(schedulers.io).observeOn(schedulers.main)
            .subscribe(SuccesObserver, FailureObserver, JobCompletedObserver)
    }
}

fun <P, R, T:Failure> CoroutineScope.launchInteractor(interactor: EitherInteractor<P,R , T>, param: P , OnResult:(Either<T, R>)->Unit): Job {
    val  job = async(interactor.dispatcher) { interactor(param) }
    return launch(interactor.ResultDispatcher) { OnResult(job.await()) }
}
class None


fun <P , R> CoroutineScope.launchInteractor(interactor:Interactor<P , R>, param: P, onResult:(R)->Unit):Job{
    val job = async(context = interactor.dispatcher){interactor(param)}
    return launch (interactor.ResultDispatcher){
        onResult(job.await())}

}
