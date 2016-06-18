package movietrailer.screens.mainscreen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by a7medM on 12/6/2016.
 */
public class SceneHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView scene_thumbnail;
    public TextView PartNumber;

    public SceneHolder(View dividerView) {
        super(dividerView);
    }

    @Override
    public void onClick(View view) {
        if (SceneAdapter.clickListener != null)
            SceneAdapter.clickListener.onClickRecyclerView(view, getAdapterPosition());
    }
}