package com.rodz.vault.adpaters;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.rodz.vault.R;

import java.io.File;
import java.util.List;

public class FullscreenImageAdapter extends RecyclerView.Adapter<FullscreenImageAdapter.ViewHolder> {

    private final List<String> imageFilenames;
    private final Context context;

    public FullscreenImageAdapter(Context context, List<String> imageFilenames) {
        this.context = context;
        this.imageFilenames = imageFilenames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fullscreen_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String filename = imageFilenames.get(position);
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, filename);
        Uri uri = Uri.fromFile(file);

        ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                .setForceStaticImage(false)
                .setDecodeAllFrames(true)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();

        holder.imageView.setController(controller);
    }

    @Override
    public int getItemCount() {
        return imageFilenames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fullscreenImageView);
        }
    }
}
