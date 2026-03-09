package com.togai.app.data.repository

import com.togai.app.data.local.dao.CategoryDao
import com.togai.app.data.local.entity.CategoryEntity
import com.togai.app.domain.model.Category
import com.togai.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpenseCategories(): Flow<List<Category>> {
        return categoryDao.getExpenseCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIncomeCategories(): Flow<List<Category>> {
        return categoryDao.getIncomeCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun insert(category: Category): Long {
        return categoryDao.insert(category.toEntity())
    }

    private fun CategoryEntity.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isDefault = isDefault,
            isIncome = isIncome
        )
    }

    private fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isDefault = isDefault,
            isIncome = isIncome
        )
    }
}
