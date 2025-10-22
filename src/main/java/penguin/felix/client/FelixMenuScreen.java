package penguin.felix.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import penguin.felix.entities.FelixMenuScreenHandler;

public class FelixMenuScreen extends HandledScreen<FelixMenuScreenHandler> {

    private static final Identifier BACKGROUND = Identifier.of("felix", "textures/menues/menu_bg.png");

    public FelixMenuScreen(FelixMenuScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 89; // 176 / 2
        this.backgroundHeight = 166;
    }

	@Override
	protected void init() {
		super.init();
	}

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(BACKGROUND, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawText(this.textRenderer, this.title.getString(), this.backgroundWidth / 2, this.y + 6, 0x404040, false);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
	}
}