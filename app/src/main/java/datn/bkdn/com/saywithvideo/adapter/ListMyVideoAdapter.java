package datn.bkdn.com.saywithvideo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import datn.bkdn.com.saywithvideo.R;
import datn.bkdn.com.saywithvideo.database.Video;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class ListMyVideoAdapter extends RealmBaseAdapter<Video> {

    private final Context mContext;
    public OnItemClicked mItemClicked;
    public OnMenuItemClicked mMenuItemClicked;

    public ListMyVideoAdapter(Context context, RealmResults<Video> videos) {
        super(context, videos);
        mContext = context;
    }

    public void setPlayButtonClicked(OnItemClicked playButtonClicked) {
        this.mItemClicked = playButtonClicked;
    }

    public void setMenuItemClicked(OnMenuItemClicked mMenuItemClicked) {
        this.mMenuItemClicked = mMenuItemClicked;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_video_profile, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvSoundName = (TextView) convertView.findViewById(R.id.tvVideoName);
            viewHolder.tvDateOfCreate = (TextView) convertView.findViewById(R.id.tvTimeVideo);
            viewHolder.imgoption = (ImageView) convertView.findViewById(R.id.imgoption);
            viewHolder.imgshare = (ImageView) convertView.findViewById(R.id.imgshare);
            viewHolder.llInfo = (LinearLayout) convertView.findViewById(R.id.llinfo);
            viewHolder.videoView = (ImageView) convertView.findViewById(R.id.videoView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imgshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClicked != null) {
                    mItemClicked.onClick(position, v);
                }
            }
        });
        viewHolder.imgoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClicked != null) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            mMenuItemClicked.onItemClick(position, item);
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.popup_list_video);
                    popupMenu.show();
                }
            }
        });

        viewHolder.llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClicked != null) {
                    mItemClicked.onClick(position, v);
                }
            }
        });
        Video video = getItem(position);
        viewHolder.tvSoundName.setText(video.getName());
        Bitmap thump = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        viewHolder.videoView.setImageBitmap(thump);
        viewHolder.tvDateOfCreate.setText(video.getTime());
        return convertView;
    }

    public interface OnItemClicked {
        void onClick(int pos, View v);
    }

    public interface OnMenuItemClicked {
        void onItemClick(int pos, MenuItem menuItem);
    }

    private class ViewHolder {
        private TextView tvSoundName;
        private ImageView videoView;
        private TextView tvDateOfCreate;
        private ImageView imgoption;
        private ImageView imgshare;
        private LinearLayout llInfo;
    }

}
