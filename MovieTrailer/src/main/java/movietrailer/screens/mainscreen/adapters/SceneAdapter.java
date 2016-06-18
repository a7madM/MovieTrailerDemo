package movietrailer.screens.mainscreen.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.movietrailer.R;

import java.util.List;

import movietrailer.screens.mainscreen.entities.Scene;


/**
 * Created by a7medM on 12/6/2015.
 */
public class SceneAdapter extends RecyclerView.Adapter<SceneHolder> {
    private List<Scene> sceneList;

    public static ViewHolderClickListener clickListener;
    Context context;


    public SceneAdapter(Context context, List<Scene> sceneList, ViewHolderClickListener clickListener) {
        this.sceneList = sceneList;
        this.clickListener = clickListener;
        this.context = context;
    }


    @Override
    public SceneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_scene, parent, false);
        SceneHolder sceneHolder = new SceneHolder(v);
        return sceneHolder;
    }

    @Override
    public void onBindViewHolder(SceneHolder holder, final int position) {
        Scene scene = sceneList.get(position);
    }

    @Override
    public int getItemCount() {
        return sceneList.size();
    }


}