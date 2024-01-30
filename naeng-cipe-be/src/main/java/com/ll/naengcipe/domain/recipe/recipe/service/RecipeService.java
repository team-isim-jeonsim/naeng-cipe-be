package com.ll.naengcipe.domain.recipe.recipe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.naengcipe.domain.ingredient.ingredient.entity.Ingredient;
import com.ll.naengcipe.domain.ingredient.ingredient.exception.IngredientNotExistException;
import com.ll.naengcipe.domain.ingredient.ingredient.repository.IngredientRepository;
import com.ll.naengcipe.domain.member.member.entity.Member;
import com.ll.naengcipe.domain.member.member.exception.UserAndWriterNotMatchException;
import com.ll.naengcipe.domain.recipe.recipe.dto.RecipeCreateRequestDto;
import com.ll.naengcipe.domain.recipe.recipe.dto.RecipeCreateResponseDto;
import com.ll.naengcipe.domain.recipe.recipe.dto.RecipeUpdateRequestDto;
import com.ll.naengcipe.domain.recipe.recipe.dto.RecipeUpdateResponseDto;
import com.ll.naengcipe.domain.recipe.recipe.entity.Recipe;
import com.ll.naengcipe.domain.recipe.recipe.entity.RecipeIngredient;
import com.ll.naengcipe.domain.recipe.recipe.exception.RecipeNotFoundException;
import com.ll.naengcipe.domain.recipe.recipe.repository.RecipeIngredientRepository;
import com.ll.naengcipe.domain.recipe.recipe.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecipeService {

	private final RecipeRepository recipeRepository;
	private final RecipeIngredientRepository recipeIngredientRepository;
	private final IngredientRepository ingredientRepository;

	@Transactional
	public RecipeCreateResponseDto addRecipe(Member member, RecipeCreateRequestDto recipeCreateDto) {

		List<Ingredient> ingredients = ingredientRepository.findByIdIn(recipeCreateDto.getIngredients());
		if (ingredientNotExist(recipeCreateDto.getIngredients().size(), ingredients.size())) {
			throw new IngredientNotExistException("해당 재료가 존재하지 않습니다.");
		}

		List<RecipeIngredient> recipeIngredients = new ArrayList<>();
		for (Ingredient ingredient : ingredients) {
			recipeIngredients.add(new RecipeIngredient(ingredient));
		}

		Recipe recipe = Recipe.createRecipe(member, recipeCreateDto.getTitle(), recipeCreateDto.getContent(),
			recipeIngredients);
		Recipe savedRecipe = recipeRepository.save(recipe);

		return RecipeCreateResponseDto.toDto(savedRecipe);
	}

	@Transactional
	public RecipeUpdateResponseDto modifyRecipe(Member member, RecipeUpdateRequestDto recipeUpdateDto) {

		List<Ingredient> ingredients = ingredientRepository.findByIdIn(recipeUpdateDto.getIngredients());
		//recipeCreateDto의 재료가 DB에 없으면 예외처리
		if (ingredientNotExist(recipeUpdateDto.getIngredients().size(), ingredients.size())) {
			throw new IngredientNotExistException("해당 재료가 존재하지 않습니다.");
		}

		//레시피를 DB에서 조회 -> 레시피가 존재하지 않으면 예외처리
		Recipe foundRecipe = recipeRepository.findByIdWithRecipeIngredient(recipeUpdateDto.getId())
			.orElseThrow(() -> new RecipeNotFoundException("해당 레시피가 존재하지 않습니다."));

		//레시피 작성자와 요청자가 다르면 예외처리
		if (!foundRecipe.getMember().getId().equals(member.getId())) {
			throw new UserAndWriterNotMatchException("해당 레시피에 대한 수정 권한이 없습니다.");
		}

		//레시피에 속한 재료 모두 제거
		recipeIngredientRepository.deleteByRecipeId(foundRecipe.getId());

		List<RecipeIngredient> recipeIngredients = new ArrayList<>();
		for (Ingredient ingredient : ingredients) {
			recipeIngredients.add(new RecipeIngredient(ingredient));
		}

		foundRecipe.change(member, recipeUpdateDto.getTitle(), recipeUpdateDto.getContent(), recipeIngredients);
		return RecipeUpdateResponseDto.toDto(foundRecipe);
	}

	private boolean
	ingredientNotExist(int requestIngredientSize, int size) {

		return size != requestIngredientSize;
	}
}
