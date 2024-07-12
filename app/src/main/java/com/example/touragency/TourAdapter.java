package com.example.touragency;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private Context context;
    private List<Tour> tourList;
    private OnAddToCartListener onAddToCartListener;
    private OnRemoveFromCartListener onRemoveFromCartListener;
    private static final String TAG = "TourAdapter";

    public interface OnAddToCartListener {
        void onAddToCart(Tour tour);
    }

    public interface OnRemoveFromCartListener {
        void onRemoveFromCart(Tour tour);
    }

    public TourAdapter(Context context, List<Tour> tourList, OnAddToCartListener onAddToCartListener, OnRemoveFromCartListener onRemoveFromCartListener) {
        this.context = context;
        this.tourList = tourList;
        this.onAddToCartListener = onAddToCartListener;
        this.onRemoveFromCartListener = onRemoveFromCartListener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);
        holder.tourName.setText(tour.getName());
        holder.tourDescription.setText(tour.getDescription());
        holder.tourPrice.setText("$" + tour.getPrice());
        Picasso.get().load(tour.getImageUrl()).into(holder.tourImage);

        holder.addToCartButton.setOnClickListener(v -> {
            if (onAddToCartListener != null) {
                onAddToCartListener.onAddToCart(tour);
            }
            String tourId = tour.getId();
            if (tourId != null && !tourId.isEmpty()) {
                DatabaseReference tourRef = FirebaseDatabase.getInstance().getReference("tours").child(tourId).child("added");
                tourRef.setValue(true)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Tour added to cart successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to add tour to cart", e));
            } else {
                Log.e(TAG, "Tour ID is null or empty");
            }
        });

        holder.removeFromCartButton.setOnClickListener(v -> {
            if (onRemoveFromCartListener != null) {
                onRemoveFromCartListener.onRemoveFromCart(tour);
            }
            String tourId = tour.getId();
            if (tourId != null && !tourId.isEmpty()) {
                DatabaseReference tourRef = FirebaseDatabase.getInstance().getReference("tours").child(tourId).child("added");
                tourRef.setValue(false)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Tour removed from cart successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to remove tour from cart", e));
            } else {
                Log.e(TAG, "Tour ID is null or empty");
            }
        });
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public static class TourViewHolder extends RecyclerView.ViewHolder {
        ImageView tourImage;
        TextView tourName;
        TextView tourDescription;
        TextView tourPrice;
        Button addToCartButton;
        Button removeFromCartButton;

        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            tourImage = itemView.findViewById(R.id.tour_image);
            tourName = itemView.findViewById(R.id.tour_name);
            tourDescription = itemView.findViewById(R.id.tour_description);
            tourPrice = itemView.findViewById(R.id.tour_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
            removeFromCartButton = itemView.findViewById(R.id.remove_from_cart_button);
        }
    }
}
