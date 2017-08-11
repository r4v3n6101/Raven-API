package raven.api.client.world

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11
import raven.api.common.utils.BlockData
import raven.api.common.utils.iterator
import raven.api.common.world.WorldSnapshot

/**
 * Created by r4v3n6101 on 13.06.2016.
 */
@Deprecated("Unstable")
class WorldSnapshotRenderer(val snapshot: WorldSnapshot) {

    val camera: Camera
    private var displayLists: Int? = null

    init {
        camera = Camera(256, 256, 0.005F, 96F)
    }

    fun render(partialTime: Float) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glColor4f(1F, 1F, 1F, 1F)
        camera.setupTransform(partialTime)
        renderBlocks()
        snapshot.entities.forEach { renderEntity(it) }
        snapshot.tiles.forEach { renderTileEntity(it) }
    }

    private fun renderTileEntity(tile: TileEntity) {
        if (TileEntityRendererDispatcher.instance.hasSpecialRenderer(tile)) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(
                    tile,
                    tile.xCoord.toDouble(),
                    tile.yCoord.toDouble(),
                    tile.zCoord.toDouble(),
                    1F
            )
            GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        }
    }

    private fun renderEntity(ent: Entity) {
        val render = RenderManager.instance.getEntityRenderObject(ent)
        if (render != null) {
            render.doRender(
                    ent,
                    ent.prevPosX,
                    ent.prevPosY,
                    ent.prevPosZ,
                    1F,
                    1F
            )
            GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        }
    }

    private fun renderBlocks() {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture)
        if (displayLists == null) {
            displayLists = GLAllocation.generateDisplayLists(1)
            renderBlocks.blockAccess = snapshot.chunkCache
            GL11.glNewList(displayLists as Int, GL11.GL_COMPILE)
            for (pos in snapshot.worldBounds) {
                renderBlock(BlockData(snapshot.chunkCache, pos))
            }
            GL11.glEndList()
        } else {
            GL11.glCallList(displayLists as Int)
        }
    }

    private fun renderBlock(block: BlockData) {
        val b = block.block
        if (b != null) {
            val tessellator = Tessellator.instance
            tessellator.startDrawingQuads()
            renderBlocks.renderBlockByRenderType(b, block.x, block.y, block.z)
            tessellator.draw()
        }
    }

    val renderBlocks by lazy { RenderBlocks() }
}