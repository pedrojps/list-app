package com.example.list_app.data.repository;

import com.example.list_app.data.entities.PullRequests;
import com.example.list_app.data.entities.RepositoriesList;
import com.example.list_app.data.network.Resource;
import com.example.list_app.data.souce.RepositoriesListRemoteDataSource;

import java.util.List;

import io.reactivex.Single;

public class RepositoriesListRepository {

    private static RepositoriesListRepository INSTANCE;

    private final RepositoriesListRemoteDataSource mRepositoriesListRemoteDataSource;


    private RepositoriesListRepository() {
        mRepositoriesListRemoteDataSource = RepositoriesListRemoteDataSource.getInstance();
    }

    public static synchronized RepositoriesListRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RepositoriesListRepository();
        }
        return INSTANCE;
    }

    public void clearInstance() {
        INSTANCE = null;
    }


    public Single<Resource<RepositoriesList>> getRepositoriesList(int page) {
        return mRepositoriesListRemoteDataSource.getRepositoriesList(page);
    }

    public Single<Resource<List<PullRequests>>> getPullRequestList(String name,String login) {
        return mRepositoriesListRemoteDataSource.getPullRequestList(name,login);
    }

}
