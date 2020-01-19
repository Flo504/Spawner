package fr.flo504.spawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TagUtils {

    private final static List<String> spawnerTags;

    static{
        spawnerTags = new ArrayList<>();
        for(String tag : Arrays.asList(
                "MaxNearbyEntities",
                "RequiredPlayerRange",
                "SpawnCount",
                "SpawnData",
                "MaxSpawnDelay",
                "id",
                "SpawnRange",
                "MinSpawnDelay",
                "SpawnPotentials")){
            spawnerTags.add(tag);
        }

        if(Commons.version <= 8){
            spawnerTags.add("EntityId");
        }
    }

    private final static Field itemTagField;
    private final static Constructor<?> nbtTagCompoundConstructor;
    private final static Constructor<?> blockPositionConstructor;
    private final static Method getHandleWorldMethod;
    private final static Method getTileEntityMethod;
    private final static Method getNBTTagCompoundMethod;
    private final static Method setNBTTagCompoundMethod;
    private final static Method asNMSCopyMethod;
    private final static Class<?> nmsItemStackClass;
    private final static Method asBukkitCopyMethod;
    private final static Class<?> tileEntityMobSpawnerClass;
    private final static Method saveMethod;
    private final static Method loadMethod;

    static{
        nmsItemStackClass = Reflect.getClass(Commons.npack+"ItemStack");
        itemTagField = Reflect.getField(nmsItemStackClass, "tag");
        itemTagField.setAccessible(true);
        final Class<?> nmsNBTTagCompoundClass = Reflect.getClass(Commons.npack+"NBTTagCompound");
        nbtTagCompoundConstructor = Reflect.getConstructor(nmsNBTTagCompoundClass);
        final Class<?> blockPositionClass = Reflect.getClass(Commons.npack+"BlockPosition");
        blockPositionConstructor = Reflect.getConstructor(blockPositionClass, int.class, int.class, int.class);
        final Class<?> craftWorldClass = Reflect.getClass(Commons.cpack+"CraftWorld");
        getHandleWorldMethod = Reflect.getMethod(craftWorldClass, "getHandle");
        final Class<?> nmsWorldClass = Reflect.getClass(Commons.npack+"World");
        getTileEntityMethod = Reflect.getMethod(nmsWorldClass, "getTileEntity", blockPositionClass);
        getNBTTagCompoundMethod = Reflect.getMethod(nmsNBTTagCompoundClass, "get", String.class);
        final Class<?> nmsNBTBase = Reflect.getClass(Commons.npack + "NBTBase");
        setNBTTagCompoundMethod = Reflect.getMethod(nmsNBTTagCompoundClass, "set", String.class, nmsNBTBase);
        final Class<?> craftItemStackClass = Reflect.getClass(Commons.cpack+"inventory.CraftItemStack");
        asNMSCopyMethod = Reflect.getMethod(craftItemStackClass, "asNMSCopy", ItemStack.class);
        asBukkitCopyMethod = Reflect.getMethod(craftItemStackClass, "asBukkitCopy", nmsItemStackClass);
        tileEntityMobSpawnerClass = Reflect.getClass(Commons.npack+"TileEntityMobSpawner");
        saveMethod = Reflect.getMethod(tileEntityMobSpawnerClass, Commons.version >= 9  ? "save" : "b", nmsNBTTagCompoundClass);
        loadMethod = Reflect.getMethod(tileEntityMobSpawnerClass, Commons.version >= 13 ? "load" : "a", nmsNBTTagCompoundClass);
    }


    public static ItemStack applyDataToItemStack(ItemStack spawnerItem, Location spawnerBlockLocation){
        Objects.requireNonNull(spawnerItem, "The spawner itemStack can not be null");
        Objects.requireNonNull(spawnerBlockLocation, "The spawner location can not be null");

        final Object tileEntity = getTileEntityAt(spawnerBlockLocation);

        final Object nbtTagCompound = newNBTTag();

        requireAdequate(tileEntityMobSpawnerClass, tileEntity);

        Reflect.invoke(saveMethod, tileEntity, nbtTagCompound);

        final Object nmsSpawnerItem = getNMSItemStack(spawnerItem);

        final Object itemTags = getOrCreateItemTag(nmsSpawnerItem);

        for(String tag : spawnerTags){
            Reflect.invoke(setNBTTagCompoundMethod, itemTags, tag, Reflect.invoke(getNBTTagCompoundMethod, nbtTagCompound, tag));
        }

        setItemTag(nmsSpawnerItem, itemTags);

        final ItemStack finalItemStack = getBukkitItemStack(nmsSpawnerItem);

        return finalItemStack;
    }

    public static void applyDataToBlockSpawner(ItemStack spawnerItem, Location spawnerBlockLocation){
        Objects.requireNonNull(spawnerItem, "The spawner itemStack can not be null");
        Objects.requireNonNull(spawnerBlockLocation, "The spawner location can not be null");

        final Object nmsSpawnerItem = getNMSItemStack(spawnerItem);

        final Object nbtTagCompound = getOrCreateItemTag(nmsSpawnerItem);

        final Object tileEntity = getTileEntityAt(spawnerBlockLocation);

        final Object spawnerNBTTag = newNBTTag();

        requireAdequate(tileEntityMobSpawnerClass, tileEntity);

        for(String tag : spawnerTags){
            Reflect.invoke(setNBTTagCompoundMethod, spawnerNBTTag, tag, Reflect.invoke(getNBTTagCompoundMethod, nbtTagCompound, tag));
        }

        Reflect.invoke(loadMethod, tileEntity, spawnerNBTTag);
    }

    public static Object getItemTag(Object nmsItemStack){
        Objects.requireNonNull(nmsItemStack, "The itemStack can not be null");
        requireAdequate(nmsItemStackClass, nmsItemStack);
        return Reflect.get(itemTagField, nmsItemStack);
    }

    public static Object getOrCreateItemTag(Object nmsItemStack){
        final Object itemTag = getItemTag(nmsItemStack);
        return itemTag == null ? newNBTTag() : itemTag;
    }

    public static void setItemTag(Object nmsItemStack, Object itemTag){
        Objects.requireNonNull(nmsItemStack, "The itemStack can not be null");
        requireAdequate(nmsItemStackClass, nmsItemStack);
        Reflect.set(itemTagField, nmsItemStack, itemTag);
    }

    public static Object newNBTTag(){
        return Reflect.newInstance(nbtTagCompoundConstructor);
    }

    public static Object getTileEntityAt(Location location){
        Objects.requireNonNull(location, "The location can not be null");
        return Reflect.invoke(getTileEntityMethod, getNMSWorld(location.getWorld()), getBlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public static Object getNMSWorld(World world){
        Objects.requireNonNull(world, "The world can not be null");
        return Reflect.invoke(getHandleWorldMethod, world);
    }

    public static Object getBlockPosition(int x, int y, int z){
        return Reflect.newInstance(blockPositionConstructor, x, y, z);
    }

    public static Object getNMSItemStack(ItemStack itemStack){
        Objects.requireNonNull(itemStack, "The itemStack can not be null");
        return Reflect.invokeStatic(asNMSCopyMethod, itemStack);
    }

    public static ItemStack getBukkitItemStack(Object nmsItemStack){
        Objects.requireNonNull(nmsItemStack, "The itemStack can not be null");
        requireAdequate(nmsItemStackClass, nmsItemStack);
        return (ItemStack) Reflect.invokeStatic(asBukkitCopyMethod, nmsItemStack);
    }

    public static void requireAdequate(Class<?> clazz, Object arg, String message){
        if(!clazz.isInstance(arg)){
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireAdequate(Class<?> clazz, Object arg){
        requireAdequate(clazz, arg, "The object must be an instance of the "+clazz);
    }

    public final static class Commons{

        public static final String versionName = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        public static final int version = Integer.parseInt(versionName.substring(1).split("_")[1]);
        public static final String npack = "net.minecraft.server." + versionName + ".";
        public static final String cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
    }

}
