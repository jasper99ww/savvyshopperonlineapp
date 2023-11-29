package com.example.savvyshopperonlineapp.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE item_id =:itemId")
    fun getItem(itemId:Int):Flow<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Transaction
    suspend fun insertAndGetId(item: Item): Long {
        insert(item)
        return getLastInsertId()
    }

    @Query("SELECT last_insert_rowid()")
    suspend fun getLastInsertId(): Long
}

@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(shoppingList: ShoppingList)

    @Query("""
        SELECT * FROM items AS I
        INNER JOIN shopping_list AS S
        ON I.listIdForeignKey = s.list_id
        WHERE s.list_id =:listId
    """)
    fun getItemsWithList(listId: Int):Flow<List<ItemsWithList>>

    @Query("""
        SELECT * FROM items AS I
        INNER JOIN shopping_list AS S
        ON I.listIdForeignKey = S.list_id
        WHERE I.listIdForeignKey =:listId
    """)
    fun getItemsWithListFilteredById(listId: Int):Flow<List<ItemsWithList>>

    @Query("""
        SELECT * FROM items AS I
        INNER JOIN shopping_list AS S
        ON I.listIdForeignKey = S.list_id
        WHERE I.item_id =:itemId
    """)
    fun getItemWithListFilteredById(itemId: Int):Flow<ItemsWithList>

    @Query("""
        SELECT * FROM items AS I
        INNER JOIN shopping_list AS S
        ON I.listIdForeignKey = S.list_id
    """)
    fun getItemsWithList():Flow<List<ItemsWithList>>
}

data class ItemsWithList(
    @Embedded val item: Item,
    @Embedded val shoppingList: ShoppingList
)