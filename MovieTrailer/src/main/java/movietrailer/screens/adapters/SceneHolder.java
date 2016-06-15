package movietrailer.screens.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.movietrailer.R;

/**
 * Created by a7medM on 12/6/2016.
 */
public class SceneHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView scene_thumbnail;
    public TextView PartNumber;

    public SceneHolder(View dividerView) {
        super(dividerView);
        scene_thumbnail = (ImageView) dividerView.findViewById(R.id.scene_thumbnail);
    }

    @Override
    public void onClick(View view) {
        if (SceneAdapter.clickListener != null)
            SceneAdapter.clickListener.onClickRecyclerView(view, getAdapterPosition());
    }
}