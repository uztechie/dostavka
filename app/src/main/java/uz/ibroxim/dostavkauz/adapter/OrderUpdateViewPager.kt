package uz.ibroxim.dostavkauz.adapter

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.ibroxim.dostavkauz.fragments.driver.OrderUpdateAudioFragment
import uz.ibroxim.dostavkauz.fragments.driver.OrderUpdatePassportFragment
import uz.ibroxim.dostavkauz.fragments.driver.OrderUpdatePaymentFragment
import uz.ibroxim.dostavkauz.fragments.driver.OrderUpdateReceiverInfoFragment

class OrderUpdateViewPager(fragmentManager: FragmentManager, lifecycle:Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle)

{
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> OrderUpdateReceiverInfoFragment()
            1-> OrderUpdatePassportFragment()
            2-> OrderUpdateAudioFragment()
            3-> OrderUpdatePaymentFragment()
            else-> throw Resources.NotFoundException("Position not found")
        }
    }
}