package grondag.renderbender.init;


import java.util.Random;
import java.util.function.Supplier;

import grondag.renderbender.init.BasicBlocks.BeTestBlockEntity;
import grondag.renderbender.model.MeshTransformer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

class BeTestTransform implements MeshTransformer {
    static RenderMaterial matSolid = RendererAccess.INSTANCE.getRenderer().materialFinder()
            .blendMode(0, BlendMode.SOLID).find();
    
    static RenderMaterial matSolidGlow = RendererAccess.INSTANCE.getRenderer().materialFinder()
            .blendMode(0, BlendMode.SOLID).disableDiffuse(0, true).emissive(0, true).disableAo(0, true).find();
    
    static RenderMaterial matTrans = RendererAccess.INSTANCE.getRenderer().materialFinder()
            .blendMode(0, BlendMode.TRANSLUCENT).find();
    
    static RenderMaterial matTransGlow = RendererAccess.INSTANCE.getRenderer().materialFinder()
            .blendMode(0, BlendMode.TRANSLUCENT).disableDiffuse(0, true).emissive(0, true).disableAo(0, true).find();
    
    private RenderMaterial mat = null;
    private RenderMaterial matGlow = null;
    private int stupid[];
    private boolean translucent;

    @Override
    public boolean transform(MutableQuadView q) {
        final int s = stupid == null ? -1 : stupid[q.tag()];
        final int c = translucent ? 0x80000000 | (0xFFFFFF & s) : s;
        q.material((s & 0x3) == 0 ? matGlow : mat).spriteColor(0, c, c, c, c);
        return true;
    }
    
    @Override
    public MeshTransformer prepare(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
        if(randomSupplier.get().nextInt(4) == 0) {
            mat = matTrans;
            matGlow = matTransGlow;
            translucent = true;
        } else {
            mat = matSolid;
            matGlow = matSolidGlow;
            translucent = false;
        }
        stupid = (int[])((RenderAttachedBlockView)blockView).getBlockEntityRenderAttachment(pos);
        return this;
    }
    
    @Override
    public MeshTransformer prepare(ItemStack stack, Supplier<Random> randomSupplier) {
        mat = matSolid;
        matGlow = matSolidGlow;
        translucent = false;
        stupid = BeTestBlockEntity.ITEM_COLORS;
        return this;
    }
}