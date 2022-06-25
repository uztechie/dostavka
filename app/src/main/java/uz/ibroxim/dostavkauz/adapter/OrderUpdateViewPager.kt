package uz.ibroxim.dostavkauz.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.ibroxim.dostavkauz.fragments.driver.*

class OrderUpdateViewPager(fragmentManager: FragmentManager, lifecycle:Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle)

{
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> OrderUpdateItemsFragment()
            1-> OrderUpdateAudioFragment()
            2-> OrderUpdatePaymentFragment()
            else-> throw Resources.NotFoundException("Position not found")
        }
    }
}