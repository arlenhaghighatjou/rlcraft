package atomicstryker.battletowers.common;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.Iterator;

import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerTickHandler
{
    private long time;
    
    public ServerTickHandler()
    {
        time = System.currentTimeMillis();
    }

    public void register(EventBus bus)
    {
        bus.register(TickEvent.WorldTickEvent.class, this::onTick);
    }

    public void onTick(TickEvent.WorldTickEvent tick)
    {
        if (System.currentTimeMillis() > time + 1000L) // its a one second timer OMFG
        {
        	time = System.currentTimeMillis();
        	Iterator<AS_TowerDestroyer> iter =  AS_BattleTowersCore.getTowerDestroyers().iterator();
            while(iter.hasNext())
            {
				AS_TowerDestroyer td = iter.next();
                if (td.isFinished())
                {
                    iter.remove();
                }
                else
                {
                    td.update();
                }
            }
        }
    }

}
