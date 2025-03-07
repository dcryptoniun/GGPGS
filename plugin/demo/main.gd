extends Node2D

@onready var title : Label = %Title
@onready var authstatus : Label = %AuthStatus
@onready var playerid : Label = %PlayerId
@onready var displayname : Label = %DisplayName
@onready var testlabel : Label = %TestLabel
@onready var avatarimg : TextureRect = %AvatarImg
@onready var loginbutton : Button = %LoginButton

var _plugin_name = "GGPGS"
var ggpgs_plugin

func _ready() -> void:
	if Engine.has_singleton(_plugin_name):
		ggpgs_plugin = Engine.get_singleton(_plugin_name)
		ggpgs_plugin.connect("userDetailsSignal", _on_userDetailsSignal)
		ggpgs_plugin.connect("userAuthenticated", _on_user_authenticated)
		ggpgs_plugin.connect("imageStored", _on_image_stored)
	else:
		printerr("ggpgs plugin not found")
	

func _on_image_stored(image_path: String) -> void:
	print("Image stored at: ", image_path)
	
	# Use FileAccess to check if file exists first
	if not FileAccess.file_exists(image_path):
		printerr("File does not exist: ", image_path)
		return
		
	# Load image directly
	var image = Image.load_from_file(image_path)
	if image:
		var texture = ImageTexture.create_from_image(image)
		avatarimg.texture = texture
		print("Image loaded successfully")
	else:
		printerr("Failed to load image from path: ", image_path)

func _on_userDetailsSignal(data: String) -> void:
	var player_data = JSON.parse_string(data)
	if player_data:
		# Update UI with player information
		var avatar_uri = player_data.get("iconImageUri", "No Avatar")
		var display_name = player_data.get("displayName", "Unknown Player")
		var player_id = player_data.get("playerId", "No ID")
		
		playerid.text = "Player ID: " + player_id
		displayname.text = "Display Name: " + display_name
		
	else:
		print("Failed to parse player data")
		title.text = "Error: Could not load player data"
		playerid.text = ""
		displayname.text = ""

func _on_user_authenticated(is_authenticated: bool) -> void:
	if is_authenticated:
		authstatus.text = "" + "Authenticated"
		loginbutton.disabled = true
		loginbutton.text = "login successful"
	else:
		authstatus.text = "" + "Not Authenticated"
	#testlabel.text = "Authentication Status: " + ("Authenticated" if is_authenticated else "Not Authenticated")

func _on_login_button_pressed() -> void:
	if ggpgs_plugin:
		ggpgs_plugin.login()
