package net.euse.calendar.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import net.euse.calendar.R
import net.euse.calendar.activities.SimpleActivity
import net.euse.calendar.dialogs.DeleteEventDialog
import net.euse.calendar.extensions.dbHelper
import net.euse.calendar.extensions.shareEvents
import net.euse.calendar.helpers.Formatter
import net.euse.calendar.models.Event
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.event_item_day_view.view.*


class EventsListAdapter(activity: SimpleActivity, val events: ArrayList<Event>, recyclerView: MyRecyclerView, itemClick: (Any) -> Unit)
    : MyRecyclerViewAdapter(activity, recyclerView, null, itemClick) {

    override fun getActionMenuId() = R.menu.cab_day

    override fun prepareActionMode(menu: Menu) {}

    override fun prepareItemSelection(view: View) {}

    override fun markItemSelection(select: Boolean, view: View?) {
        view?.event_item_frame?.isSelected = select
    }

    override fun actionItemPressed(id: Int) {
        when (id) {
            R.id.cab_share -> shareEvents()
            R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = events.size

    override fun getItemViewType(position: Int): Int {
        if (itemCount<=0) return -1
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =(
        if (viewType!=-1)
            createViewHolder(R.layout.event_item_day_view, parent)
        else
            createViewHolder(R.layout.empty_recycler_view, parent)
        )

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val event = events[position]
        val view = holder.bindView(event) { itemView, layoutPosition ->
            setupView(itemView, event)
        }
        bindViewHolder(holder, position, view)
    }

    override fun getItemCount() = events.size

    private fun setupView(view: View, event: Event) {
        view.apply {
            event_item_title.text = event.title
            event_reminder_time.text = Formatter.getTimeFromTS(context,event.startTS)

            event_reminder_time.setTextColor(textColor)
            event_item_title.setTextColor(event.color)
        }
    }

    private fun shareEvents() {
        val eventIds = ArrayList<Int>(selectedPositions.size)
        selectedPositions.forEach {
            eventIds.add(events[it].id)
        }
        activity.shareEvents(eventIds.distinct())
    }

    private fun askConfirmDelete() {
        val eventIds = ArrayList<Int>(selectedPositions.size)
        val timestamps = ArrayList<Int>(selectedPositions.size)
        selectedPositions.forEach {
            eventIds.add(events[it].id)
            timestamps.add(events[it].startTS)
        }

        DeleteEventDialog(activity, eventIds) {
            val eventsToDelete = ArrayList<Event>(selectedPositions.size)
            selectedPositions.sortedDescending().forEach {
                eventsToDelete.add(events[it])
            }
            events.removeAll(eventsToDelete)

            if (it) {
                val eventIDs = Array(eventIds.size, { i -> (eventIds[i].toString()) })
                activity.dbHelper.deleteEvents(eventIDs, true)
            } else {
                eventIds.forEachIndexed { index, value ->
                    activity.dbHelper.addEventRepeatException(value, timestamps[index], true)
                }
            }
            removeSelectedItems()
        }
    }

}
