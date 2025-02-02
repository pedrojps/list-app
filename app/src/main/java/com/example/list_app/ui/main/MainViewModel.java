package com.example.list_app.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.list_app.data.entities.Item;
import com.example.list_app.data.repository.RepositoriesListRepository;
import com.example.list_app.data.entities.RepositoriesList;
import com.example.list_app.data.network.Resource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private final RepositoriesListRepository mRepositoriesListRepository;

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> filteringText = new ObservableField<>();

    private final MutableLiveData<String> mFilterConstraint = new MutableLiveData<>();

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public final ObservableField<Resource<List<Item>>> mItems = new ObservableField<>();

    private MainAdapter mAdapter;

    private int page = 0;

    public MainViewModel(@NonNull Application application, @NonNull RepositoriesListRepository repositoriesListRepository) {
        super(application);
        mRepositoriesListRepository = repositoriesListRepository;
        loadRepositoriesList(0,true);
        setupObservables();
    }

    public ObservableField<Resource<List<Item>>> getItems() {
        return mItems;
    }

    private void loadRepositoriesList(int page,boolean isRefresh) {
        this.page = page+1;
        dataLoading.set(true);
        Disposable disposable = mRepositoriesListRepository.getRepositoriesList(this.page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::mapToItemAndImage)
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResource -> {
                    if (isRefresh && mAdapter != null){
                        mAdapter.clearData();
                    }
                    mItems.set(listResource);
                });
        addDisposable(disposable);

    }

    public void newPageRepositoriesList(){
        loadRepositoriesList(page,false);
    }

    public void refreshPageRepositoriesList(){
        loadRepositoriesList(0,true);
    }

    public MainAdapter getAdapter() {
        if(mAdapter == null)
            mAdapter = new MainAdapter(new ArrayList<>());
        return mAdapter;
    }

    private List<Item> mapToItemAndImage(Resource<RepositoriesList> equipes){
        List<Item> sortedList =new ArrayList<>();
        if(equipes.data!=null)
            sortedList = new ArrayList<>(equipes.data.items);
        return sortedList;
    }

    private void setupObservables() {
        filteringText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                setFilterConstraint(filteringText.get());
            }
        });
    }

    private void setFilterConstraint(String text) {
        mFilterConstraint.setValue(text);
    }

    public LiveData<String> getFilterConstraint() {
        return mFilterConstraint;
    }

    private void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final Application mApplication;

        private final RepositoriesListRepository mRepositoriesListRepository;

        public Factory(
                @NonNull Application application,
                @NonNull RepositoriesListRepository fundoRepository
        ) {
            mApplication = application;
            mRepositoriesListRepository = fundoRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new MainViewModel(mApplication, mRepositoriesListRepository);
        }
    }
}
