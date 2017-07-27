package munisys.net.ma.suiviactivite.adaptor;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import munisys.net.ma.suiviactivite.R;
import munisys.net.ma.suiviactivite.entities.ActiviterEmployer;

/**
 * Created by mehdibouhafs on 28/03/2017.
 */

public class AdaptorForActiviter extends RecyclerView.Adapter<AdaptorForActiviter.MyViewHolder> implements Filterable {


    ArrayList<ActiviterEmployer> list_don;
    ArrayList<ActiviterEmployer> list_don_filtred;
    Context ctx;
    OnItemClickListener mItemClickListener;

    public AdaptorForActiviter(ArrayList<ActiviterEmployer> list_don, Context ctx) {
        this.list_don = list_don;
        this.list_don_filtred = list_don;
        this.ctx = ctx;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.modele,
                parent,
                false);
        this.ctx = parent.getContext();
        MyViewHolder vh = new MyViewHolder(v);

        return  vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ActiviterEmployer activiterEmployer = list_don_filtred.get(position);

        holder.dateDebut.setText(activiterEmployer.getDate().split("\\s+")[0]);
        holder.heureDebut.setText(activiterEmployer.getHeureDebut());
        holder.heureFin.setText(activiterEmployer.getHeureFin());
        holder.duree.setText(activiterEmployer.getDuree());
        holder.client.setText(activiterEmployer.getClient());
        holder.nature.setText(activiterEmployer.getNature());
        holder.descProjet.setText(activiterEmployer.getDescProjet());
        holder.ville.setText(activiterEmployer.getVille());
        holder.lieu.setText(activiterEmployer.getLieu());
        holder.numero.setText("Intervention N°: "+activiterEmployer.getId());
        if(activiterEmployer.getTag()==0){
            Bitmap largeIcon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.send);
            holder.tag.setImageBitmap(largeIcon);
        }else{
            Bitmap largeIcon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.send1);
            holder.tag.setImageBitmap(largeIcon);
        }


    }

    @Override
    public int getItemCount() {
        //nbProduit.setText(list_don.size());
        return list_don_filtred.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();

                if (charString.isEmpty()) {

                list_don_filtred = list_don;
            } else {

                ArrayList<ActiviterEmployer> filteredList = new ArrayList<>();

                for (ActiviterEmployer activiterEmployer : list_don) {

                    if (activiterEmployer.getDate().toLowerCase().contains(charString) || activiterEmployer.getClient().toLowerCase().contains(charString) ||activiterEmployer.getNature().toLowerCase().contains(charString) || activiterEmployer.getDescProjet().toLowerCase().contains(charString) || activiterEmployer.getLieu().toLowerCase().contains(charString)
                            || activiterEmployer.getVille().toLowerCase().contains(charString)){

                        filteredList.add(activiterEmployer);
                    }
                }

                list_don_filtred = filteredList;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = list_don_filtred;
                return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list_don_filtred = (ArrayList<ActiviterEmployer>) results.values;
            notifyDataSetChanged();
        }
        };
    }


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView dateDebut;

        TextView heureDebut,heureFin,duree,client,nature,ville,descProjet,lieu,numero;
        ImageView tag;



        public MyViewHolder(View v) {
            super(v); // done this way instead of view tagging
            this.dateDebut = (TextView) v.findViewById(R.id.date);
            this.heureDebut = (TextView) v.findViewById(R.id.heureDebut);
            this.heureFin = (TextView) v.findViewById(R.id.heureFin);
            this.duree = (TextView) v.findViewById(R.id.duree);
            this.client = (TextView)v.findViewById(R.id.client);
            this.ville = (TextView)v.findViewById(R.id.ville);
            this.descProjet =(TextView) v.findViewById(R.id.description);
            this.lieu = (TextView)v.findViewById(R.id.lieu);
            this.nature = (TextView)v.findViewById(R.id.nature);
            this.client = (TextView) v.findViewById(R.id.client);
            this.numero = (TextView) v.findViewById(R.id.numero);
            this.tag = (ImageView) v.findViewById(R.id.tag);

            //v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }




    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final AdaptorForActiviter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public ArrayList<ActiviterEmployer> getList_don_filtred() {
        return list_don_filtred;
    }

    public void setList_don_filtred(ArrayList<ActiviterEmployer> list_don_filtred) {
        this.list_don_filtred = list_don_filtred;
    }
}
