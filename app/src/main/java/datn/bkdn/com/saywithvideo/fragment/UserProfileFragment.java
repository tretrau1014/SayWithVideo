package datn.bkdn.com.saywithvideo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import datn.bkdn.com.saywithvideo.R;
import datn.bkdn.com.saywithvideo.activity.FavoriteActivity;
import datn.bkdn.com.saywithvideo.activity.MainActivity;
import datn.bkdn.com.saywithvideo.activity.SettingActivity;
import datn.bkdn.com.saywithvideo.activity.ShareActivity;
import datn.bkdn.com.saywithvideo.activity.ShowVideoActivity;
import datn.bkdn.com.saywithvideo.activity.SoundActivity;
import datn.bkdn.com.saywithvideo.activity.SoundBoardActivity;
import datn.bkdn.com.saywithvideo.adapter.ListMyVideoAdapter;
import datn.bkdn.com.saywithvideo.database.RealmUtils;
import datn.bkdn.com.saywithvideo.model.Video;
import datn.bkdn.com.saywithvideo.utils.Utils;
import io.realm.RealmResults;

public class UserProfileFragment extends Fragment implements View.OnClickListener, ListMyVideoAdapter.OnItemClicked {

    private boolean mIsVolume;
    private ListView mLvMyVideo;
    private ImageView mImgVolume;
    private LinearLayout mLnSound;
    private LinearLayout mLnSoundboards;
    private LinearLayout mLnFavorites;
    private LinearLayout mLlCreateDub;
    private TextView mTvCreateDub;
    private TextView mTvUserName;
    private TextView mNumFavorite;
    private TextView mNumSound;
    private TextView mNumSoundBoard;
    private ImageView mImgBackgroundVideo;
    private RealmResults<Video> mVideos;
    private ListMyVideoAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private int mFavorite;

    public static UserProfileFragment newInstance() {

        Bundle args = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavorite = RealmUtils.getRealmUtils(getContext()).getFavoriteSound(getContext()).size();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean favorite = intent.getBooleanExtra("Favorite", false);
                mFavorite = favorite ? mFavorite + 1 : mFavorite - 1;
                mNumFavorite.setText(mFavorite + "");
            }
        };
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("Favorite"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.from(getContext()).inflate(R.layout.fragment_user_profile, container, false);

        mLnSound = (LinearLayout) v.findViewById(R.id.lnSounds);
        mLnSoundboards = (LinearLayout) v.findViewById(R.id.lnSoundboards);
        mLnFavorites = (LinearLayout) v.findViewById(R.id.lnFavorites);
        mLlCreateDub = (LinearLayout) v.findViewById(R.id.llCreateDub);
        mTvCreateDub = (TextView) v.findViewById(R.id.tvCreateDub);
        mTvUserName = (TextView) v.findViewById(R.id.tvNameUser);
        mNumFavorite = (TextView) v.findViewById(R.id.tvNumberSoundFavorite);
        mNumSound = (TextView) v.findViewById(R.id.tvNumberSound);
        mNumSoundBoard = (TextView) v.findViewById(R.id.tvNumberSoundBoards);
        mImgVolume = (ImageView) v.findViewById(R.id.imgVolume);
        mImgBackgroundVideo = (ImageView) v.findViewById(R.id.imgBackgroundVideo);
        mLvMyVideo = (ListView) v.findViewById(R.id.lvMyDubs);

        mVideos = RealmUtils.getRealmUtils(getContext()).getVideo(getContext());
        if (mVideos.size() != 0) {
            mLlCreateDub.setVisibility(View.INVISIBLE);
        }
        mAdapter = new ListMyVideoAdapter(getContext(), mVideos);
        mAdapter.setPlayButtonClicked(this);
        mLvMyVideo.setAdapter(mAdapter);
        init();
        return v;
    }

    private void init() {
        mLnSound.setOnClickListener(this);
        mLnSoundboards.setOnClickListener(this);
        mLnFavorites.setOnClickListener(this);
        mTvUserName.setOnClickListener(this);
        mTvCreateDub.setOnClickListener(this);
        mImgVolume.setOnClickListener(this);
        mLlCreateDub.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTvUserName.setText(Utils.getCurrentUserName(getContext()));
        int numsound = RealmUtils.getRealmUtils(getContext()).getSoundOfUser(getContext(), Utils.getCurrentUserID(getContext())).size();
        mNumFavorite.setText("" + mFavorite);
        mNumSound.setText(numsound + "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnSounds:
                startActivity(new Intent(getContext(), SoundActivity.class));
                break;
            case R.id.lnSoundboards:
                startActivity(new Intent(getContext(), SoundBoardActivity.class));
                break;
            case R.id.lnFavorites:
                startActivity(new Intent(getContext(), FavoriteActivity.class));
                break;
            case R.id.tvNameUser:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.tvCreateDub:
                ((MainActivity) getContext()).showSounds();
                break;
            case R.id.imgVolume:
                mIsVolume = !mIsVolume;
                mImgVolume.setImageResource(mIsVolume ? R.mipmap.ic_action_volume_on : R.mipmap.ic_action_volume_muted);
                break;
        }
    }

    /**
     * List video event
     *
     * @param pos
     * @param v
     */
    @Override
    public void onClick(int pos, View v) {
        Video video = mVideos.get(pos);
        switch (v.getId()) {
            case R.id.llinfo:
                Intent intent = new Intent(getActivity(), ShowVideoActivity.class);
                intent.putExtra("VideoPath", video.getPath());
                startActivity(intent);
                break;
            case R.id.imgshare:
                Intent i = new Intent(getActivity(), ShareActivity.class);
                i.putExtra("filePath", video.getPath());
                startActivity(i);
                break;
            case R.id.imgoption:
                break;
        }
    }
}
