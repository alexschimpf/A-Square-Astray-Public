package com.tendersaucer.asquareastray.level;

public class CollisionFilters {

    public static final short DEFAULT_CATEGORY = 0x0001;
    public static final short DEFAULT_MASK = -1;
    public static final short LINE_CATEGORY = 0x0002;
    public static final short LINE_MASK = DEFAULT_CATEGORY;
    public static final short PROJECTILE_CATEGORY = 0x0004;
    public static final short PROJECTILE_MASK = DEFAULT_CATEGORY;
}
