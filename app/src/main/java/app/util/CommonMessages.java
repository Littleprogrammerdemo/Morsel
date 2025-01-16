package app.util;

public class CommonMessages {

    public static final String RECIPE_CREATION_SUCCESS = """
            <html>
            <body>
            Congratulations! Your recipe has been successfully created.<br><br>
            Recipe Name: %s<br>
            Ingredients: %s<br>
            Instructions: %s<br>
            <br>
            We hope you enjoy your creation!<br>
            Regards,<br>
            Recipe App Team
            </body>
            </html>
            """;

    public static final String RECIPE_UPDATE_SUCCESS = """
            <html>
            <body>
            Your recipe has been successfully updated.<br><br>
            Recipe Name: %s<br>
            Ingredients: %s<br>
            Instructions: %s<br>
            <br>
            Regards,<br>
            Recipe App Team
            </body>
            </html>
            """;

    public static final String RECIPE_DELETION_SUCCESS = "The recipe '%s' has been successfully deleted!";
    public static final String RECIPE_NOT_FOUND = "Recipe with ID='%s' not found.";
    public static final String RECIPE_ALREADY_EXISTS = "A recipe with the name '%s' already exists.";
    public static final String INGREDIENT_NOT_FOUND = "Ingredient '%s' not found in the recipe.";
    public static final String INVALID_RECIPE_ID = "Invalid recipe ID='%s'.";
    public static final String INVALID_INGREDIENT_QUANTITY = "Quantity for ingredient '%s' must be a positive number.";
    public static final String RECIPE_NOT_PUBLISHED_YET = "Recipe with ID='%s' has not been published yet.";
    public static final String RECIPE_ALREADY_PUBLISHED = "Recipe with ID='%s' is already published.";
    public static final String RECIPE_SUBMISSION_PENDING = "Your recipe with ID='%s' is under review and will be published once approved.";
    public static final String RECIPE_SUBMISSION_APPROVED = "Congratulations! Your recipe with ID='%s' has been approved and published.";
    public static final String RECIPE_SUBMISSION_REJECTED = "Your recipe with ID='%s' has been rejected. Please review the feedback for improvements.";

    public static final String RECIPE_CATEGORY_NOT_FOUND = "Recipe category '%s' not found.";
    public static final String INVALID_CATEGORY = "Category '%s' is not valid.";

    public static final String NO_RECIPES_FOUND = "No recipes found for the specified search criteria.";
    public static final String INVALID_RATING = "Invalid rating '%s'. Rating must be between 1 and 5.";
    public static final String RECIPE_RATED_SUCCESSFULLY = "Recipe '%s' rated successfully with a score of %s.";
    public static final String USER_NOT_AUTHORIZED_TO_RATE = "User with ID='%s' is not authorized to rate this recipe.";

    public static final String USER_ACCOUNT_CREATED = """
            <html>
            <body>
            Welcome %s %s,<br><br>
            Your Recipe App account has been successfully created!<br>
            Please use the following details to log in:<br><br>
            - Email: %s<br>
            - Temporary Password: %s<br>
            <br>
            <b>Important note:</b> You must change your password after your first login. Please use the reset password option.<br><br>
            Regards,<br>
            Recipe App Support Team
            </body>
            </html>
            """;

    public static final String PASSWORD_SUCCESSFULLY_CHANGED = """
            <html>
            <body>
            Congratulations %s %s,<br><br>
            <b>Your password has been successfully changed!</b><br><br>
            Regards,<br>
            Recipe App Support Team
            </body>
            </html>
            """;

    public static final String ALREADY_EXISTING_EMAIL = "An account with email '%s' already exists.";
    public static final String USER_DOES_NOT_EXIST = "User with ID='%s' does not exist.";
    public static final String INVALID_USER_TYPE = "User with ID='%s' is not a valid %s.";
    public static final String ACCOUNT_LOCKED = "Account with email '%s' is locked due to too many failed login attempts.";
    public static final String INVALID_PASSWORD = "The given password is incorrect.";
    public static final String EMAIL_CANT_BE_SENT = "Email could not be sent due to an unknown error.";

    public static final String INVALID_RECIPE_FORMAT = "Invalid recipe format. Please ensure all fields are correctly filled.";
    public static final String RECIPE_NOT_PUBLISHED = "Recipe with ID='%s' has not been published yet.";
    public static final String INGREDIENT_ALREADY_EXISTS = "Ingredient '%s' already exists in the recipe.";
    public static final String RECIPE_NOT_FOUND_BY_NAME = "No recipe found with the name '%s'.";

    public static final String RECIPE_NOT_REVIEWED_YET = "Recipe with ID='%s' is still under review.";
    public static final String RECIPE_ALREADY_REVIEWED = "Recipe with ID='%s' has already been reviewed.";

    public static final String NO_INGREDIENTS_IN_RECIPE = "Recipe with ID='%s' does not contain any ingredients.";
    public static final String INGREDIENT_ADDED_TO_RECIPE = "Ingredient '%s' successfully added to recipe '%s'.";
    public static final String INGREDIENT_REMOVED_FROM_RECIPE = "Ingredient '%s' successfully removed from recipe '%s'.";

    public static final String INVALID_RECIPE_DETAILS = "Please provide valid details for the recipe.";
    public static final String INVALID_RECIPE_ID_OR_CATEGORY = "Invalid recipe ID or category.";
    public static final String INVALID_DATA_PROVIDED = "Invalid data provided!";

}
