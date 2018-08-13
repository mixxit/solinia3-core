package com.solinia.solinia;
import org.bukkit.event.entity.EntityDamageEvent;

import me.yamakaja.runtimetransformer.annotation.Transform;

@Transform(EntityDamageEvent.class)
public class EntityDamageEventTransformer {

	/*
    @Inject(InjectionType.INSERT)
    public void setCancelled(boolean value) {
        if (value)
            Thread.dumpStack();
        throw null;
    }
	 */
}