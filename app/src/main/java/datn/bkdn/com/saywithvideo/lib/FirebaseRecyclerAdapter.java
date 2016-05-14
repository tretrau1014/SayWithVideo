package datn.bkdn.com.saywithvideo.lib;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import datn.bkdn.com.saywithvideo.R;
import datn.bkdn.com.saywithvideo.firebase.FirebaseConstant;

/**
 * Created by Matteo on 24/08/2015.
 * <p/>
 * This class is a generic way of backing an Android RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location.
 * It marshals received data into the given class type.
 * Extend this class and provide an implementation of the abstract methods, which will notify when
 * the adapter list changes.
 * <p/>
 * This class also simplifies the management of configuration change (e.g.: device rotation)
 * allowing the restore of the list.
 *
 * @param <T> The class type to use as a model for the data contained in the children of the
 *            given Firebase location
 */
public abstract class FirebaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<ViewHolder> {
    private HashMap<String, String> mUserNames;
    private Query mQuery;
    private Class<T> mItemClass;
    private ProgressDialog mProgressDialog;
    private ArrayList<T> mItems;
    private ArrayList<String> mKeys;
    private ArrayList<String> mFavorites;
    private Firebase mUserQuery;
    /**
     * @param query     The Firebase location to watch for data changes.
     *                  Can also be a slice of a location, using some combination of
     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     * @param itemClass The class of the items.
     */
//    public FirebaseRecyclerAdapter(Query query, Class<T> itemClass) {
//        this(query, itemClass, null, null);
//    }

    /**
     * @param query     The Firebase location to watch for data changes.
     *                  Can also be a slice of a location, using some combination of
     *                  <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>.
     * @param itemClass The class of the items.
     * @param items     List of items that will load the adapter before starting the listener.
     *                  Generally null or empty, but this can be useful when dealing with a
     *                  configuration change (e.g.: reloading the adapter after a device rotation).
     *                  Be careful: keys must be coherent with this list.
     * @param keys      List of keys of items that will load the adapter before starting the listener.
     *                  Generally null or empty, but this can be useful when dealing with a
     *                  configuration change (e.g.: reloading the adapter after a device rotation).
     *                  Be careful: items must be coherent with this list.
     */
    public FirebaseRecyclerAdapter(Context context, Query query, @Nullable Query mQuery, Class<T> itemClass,
                                   @Nullable ArrayList<T> items,
                                   @Nullable ArrayList<String> keys) {
        this.mQuery = query;
        if (items != null && keys != null) {
            this.mItems = items;
            this.mKeys = keys;
        } else {
            mItems = new ArrayList<>();
            mKeys = new ArrayList<>();
        }
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        query.addListenerForSingleValueEvent(valueEventListener);
        this.mItemClass = itemClass;
        mUserQuery = new Firebase(FirebaseConstant.BASE_URL + FirebaseConstant.USER_URL);
        mUserQuery.addListenerForSingleValueEvent(mUserListener);
        if (mQuery != null) {
            mQuery.addChildEventListener(mListenner);
        }
        query.addChildEventListener(mListener);
    }

    private ValueEventListener mUserListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("FirebaseAdapter","onDataChange");
            for(DataSnapshot data : dataSnapshot.getChildren()){
                final String key = data.getKey();
                if (mUserNames == null) {
                    mUserNames = new HashMap<>();
                }
                String name = data.child("name").getValue().toString();
                mUserNames.put(key, name);
            }

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private ChildEventListener mListenner = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d("FirebaseAdapter","onDataChange.Favorite");
            if (mFavorites == null) {
                mFavorites = new ArrayList<>();
            }
            String key = dataSnapshot.getKey();
            if (!mFavorites.contains(key)) {
                mFavorites.add(key);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            mFavorites.remove(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };


    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("FirebaseAdapter","end loading");
            mProgressDialog.dismiss();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("FirebaseAdapter","onChildAdded");
            String key = dataSnapshot.getKey();
            T item = dataSnapshot.getValue(FirebaseRecyclerAdapter.this.mItemClass);
            int insertedPosition;
            if (!mKeys.contains(key)) {
                if (previousChildName == null) {
                    mItems.add(0, item);
                    mKeys.add(0, key);
                    insertedPosition = 0;
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mItems.size()) {
                        mItems.add(item);
                        mKeys.add(key);
                    } else {
                        mItems.add(nextIndex, item);
                        mKeys.add(nextIndex, key);
                    }
                    insertedPosition = nextIndex;
                }
                notifyItemInserted(insertedPosition);
                itemAdded(item, key, insertedPosition);
            } else {
                int currentPosition = mKeys.indexOf(key);
                itemExist(item, key, currentPosition);

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
//
            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                T oldItem = mItems.get(index);
                T newItem = dataSnapshot.getValue(FirebaseRecyclerAdapter.this.mItemClass);
                //  mItems.set(index, newItem);
                notifyItemChanged(index);
                itemChanged(oldItem, newItem, key, index);
            }
        }


        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();

            if (mKeys.contains(key)) {
                int index = mKeys.indexOf(key);
                T item = mItems.get(index);

                mKeys.remove(index);
                mItems.remove(index);

                notifyItemRemoved(index);
                itemRemoved(item, key, index);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();

            int index = mKeys.indexOf(key);
            T item = dataSnapshot.getValue(FirebaseRecyclerAdapter.this.mItemClass);
            mItems.remove(index);
            mKeys.remove(index);
            int newPosition;
            if (previousChildName == null) {
                mItems.add(0, item);
                mKeys.add(0, key);
                newPosition = 0;
            } else {
                int previousIndex = mKeys.indexOf(previousChildName);
                int nextIndex = previousIndex + 1;
                if (nextIndex == mItems.size()) {
                    mItems.add(item);
                    mKeys.add(key);
                } else {
                    mItems.add(nextIndex, item);
                    mKeys.add(nextIndex, key);
                }
                newPosition = nextIndex;
            }
            notifyItemMoved(index, newPosition);
            itemMoved(item, key, index, newPosition);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur.");
        }

    };

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, final int position);

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    public void destroy() {
        mQuery.removeEventListener(mListener);
    }

    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    public ArrayList<T> getItems() {
        return mItems;
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    public ArrayList<String> getKeys() {
        return mKeys;
    }

    public HashMap<String, String> getUsernames() {
        return mUserNames;
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    public T getItem(int position) {
        return mItems.get(position);
    }

    public ArrayList<String> getmFavorites() {
        return mFavorites;
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    public int getPositionForItem(T item) {
        return mItems != null && mItems.size() > 0 ? mItems.indexOf(item) : -1;
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    public boolean contains(T item) {
        return mItems != null && mItems.contains(item);
    }

    /**
     * Called after an item has already exist
     *
     * @param item    item exist
     * @param key     key of item
     * @param postion position of item
     */
    protected abstract void itemExist(T item, String key, int postion);
    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */

    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */

    protected abstract void itemAdded(T item, String key, int position);

    /**
     * Called after an item changed
     *
     * @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected abstract void itemChanged(T oldItem, T newItem, String key, int position);


    /**
     * Called after an item has been removed from the adapter
     *
     * @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected abstract void itemRemoved(T item, String key, int position);

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected abstract void itemMoved(T item, String key, int oldPosition, int newPosition);

}
