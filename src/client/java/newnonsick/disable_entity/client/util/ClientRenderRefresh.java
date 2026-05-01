package newnonsick.disable_entity.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

/**
 * Applies client-side rerender/reload operations after live configuration changes
 * so visual optimizations become visible immediately.
 */
public final class ClientRenderRefresh {
    private ClientRenderRefresh() {
    }

    public static void refreshAll() {
        refreshDynamicRenderers(MinecraftClient.getInstance());
    }

    public static void refreshBlockStates() {
        refreshBlockStates(MinecraftClient.getInstance());
    }

    public static void refreshBlockStates(MinecraftClient client) {
        if (client == null) {
            return;
        }

        WorldRenderer worldRenderer = client.worldRenderer;
        if (worldRenderer == null) {
            return;
        }

        scheduleVisibleChunkRebuilds(client, worldRenderer);
        worldRenderer.scheduleTerrainUpdate();
    }

    private static void refreshDynamicRenderers(MinecraftClient client) {
        if (client == null) {
            return;
        }

        WorldRenderer worldRenderer = client.worldRenderer;
        if (worldRenderer == null) {
            return;
        }

        worldRenderer.scheduleTerrainUpdate();
    }

    private static void scheduleVisibleChunkRebuilds(MinecraftClient client, WorldRenderer worldRenderer) {
        ClientWorld world = client.world;
        if (world == null) {
            worldRenderer.scheduleTerrainUpdate();
            return;
        }

        BlockPos cameraPos = client.gameRenderer.getCamera().getBlockPos();
        int centerSectionX = ChunkSectionPos.getSectionCoord(cameraPos.getX());
        int centerSectionZ = ChunkSectionPos.getSectionCoord(cameraPos.getZ());
        int viewDistance = client.options.getClampedViewDistance();

        worldRenderer.scheduleChunkRenders(
            centerSectionX - viewDistance,
            world.getBottomSectionCoord(),
            centerSectionZ - viewDistance,
            centerSectionX + viewDistance,
            world.getTopSectionCoord(),
            centerSectionZ + viewDistance
        );
    }
}
