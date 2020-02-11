package com.example.votoapp;
/**
 * Created by MD.ISRAFIL MAHMUD on 12/27/2017.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomAdapter extends BaseAdapter {
    Context context;
    String Item[];
    String SubItem[];
    String image[];
    int numero[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, String[] Item, String[] SubItem, String[] imag, int[] num) {
        this.context = context;
        this.Item = Item;
        this.SubItem = SubItem;
        this.image = imag;
        this.numero = num;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return Item.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_item_listado, null);
        TextView item = (TextView) view.findViewById(R.id.item);
        TextView subitem = (TextView) view.findViewById(R.id.subitem);
        TextView nume = (TextView) view.findViewById(R.id.textView4);
        ImageView img  = (ImageView) view.findViewById(R.id.imagenview);
        item.setText(Item[i]);
        subitem.setText(SubItem[i]);
        nume.setText(String.valueOf(numero[i]+1));
        if(image[i].equalsIgnoreCase("0")){
            img.setImageResource(R.mipmap.sincronizado);
        }
        return view;
    }
}