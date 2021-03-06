package fr.polytech.com.cinema.controller;


import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.polytech.com.cinema.entity.Actor;
import fr.polytech.com.cinema.entity.Category;
import fr.polytech.com.cinema.entity.Movie;
import fr.polytech.com.cinema.entity.Person;
import fr.polytech.com.cinema.service.CinemaApi;
import fr.polytech.com.cinema.service.RecyclerViewAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CinemaController implements Callback<List<Movie>> {
    private final String API_BASE_URL = "http://cinema.polytech-info.fr:8080/cinema-1.0/";
    private List<Movie> movieList;
    private List<Actor> actorList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public void getMovies(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;

        /**
         * LOGGING HTTP REQUEST / RESPONSE
         */

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!


        /**
         * Serializing date (timestamp produces many issues with Gson)
         */
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };


        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser)
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        CinemaApi cinemaApi = retrofit.create(CinemaApi.class);

        Call<List<Movie>> call = cinemaApi.getMovies();
        call.enqueue(this);
    }

    public void getActors(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;

        /**
         * LOGGING HTTP REQUEST / RESPONSE
         */

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!


        /**
         * Serializing date (timestamp produces many issues with Gson)
         */
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser)
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        CinemaApi cinemaApi= retrofit.create(CinemaApi.class);

        Call<List<Actor>> call = cinemaApi.getActors();

        call.enqueue(new Callback<List<Actor>>() {
            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {
                List<Actor> movieListInternal = response.body();
                if (movieListInternal != null) {
                    actorList = new ArrayList<>();

                    for(Actor actor : movieListInternal) {
                        System.out.println(actor.getPerson().getFirstname() + " " + actor.getPerson().getLastname());
                        actorList.add(actor);
                    }

                    mAdapter = new RecyclerViewAdapter(null, actorList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {
                // do nothing

                //test - to delete before release
                actorList = new ArrayList<>();

                Actor a = new Actor();
                Person p = new Person();
                p.setFirstname("Gregoire");
                p.setLastname("Piat");
                a.setPerson(p);
                a.setRole("Laveur de chiottes");
                actorList.add(a);

                a = new Actor();
                p = new Person();
                p.setFirstname("Alexandre");
                p.setLastname("Gory");
                a.setPerson(p);
                a.setRole("Attireur de clientes");
                actorList.add(a);

                mAdapter = new RecyclerViewAdapter(null, actorList);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    public void clearLists() {
        movieList = null;
        actorList = null;
    }

    @Override
    public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
        if(response.isSuccessful()) {
            List<Movie> movieListInternal = response.body();
            if (movieListInternal != null) {
                movieList = new ArrayList<>();

                for(Movie movie : movieListInternal) {
                    System.out.println(movie.getTitle());
                    movieList.add(movie);
                }

                mAdapter = new RecyclerViewAdapter(movieList, null);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Movie>> call, Throwable t) {
        t.printStackTrace();


        //test - to delete before release
        movieList = new ArrayList<>();

        Movie m = new Movie();
        m.setTitle("movie1");
        m.setReleaseDate(new Date());
        Category c = new Category();
        c.setName("cat1");
        m.setCategory(c);
        Person p = new Person();
        p.setFirstname("Steven");
        p.setLastname("Spielberg");
        m.setDirector(p);
        movieList.add(m);

        m = new Movie();
        m.setTitle("movie2");
        m.setReleaseDate(new Date());
        m.setCategory(c);
        m.setDirector(p);
        movieList.add(m);

        mAdapter = new RecyclerViewAdapter(movieList, null);
        mRecyclerView.setAdapter(mAdapter);
    }
}
