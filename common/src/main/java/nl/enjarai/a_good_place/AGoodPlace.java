package nl.enjarai.a_good_place;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class AGoodPlace {
    public static final String MOD_ID = "a_good_place";

    public static final Logger LOGGER = LogManager.getLogger("A Good Place");


    @ExpectPlatform
    public static <T extends RuleTest> Supplier<RuleTestType<T>> registerRuleTest(String id, Codec<T> codec) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void renderBlock(BakedModel model, long seed, PoseStack poseStack, MultiBufferSource buffer, BlockState state, Level level, BlockPos pos, BlockRenderDispatcher blockRenderer) {
        throw new AssertionError();
    }

    public static boolean copySamplePackIfNotPresent() {
        File file = new File(".", "resourcepacks");
        File target = new File(file, "A Good Place Sample Pack.zip");

        if (!target.exists()) {
            try (InputStream in = AGoodPlace.class.getResourceAsStream("/resourcepacks/sample_pack.zip");
                 FileOutputStream out = new FileOutputStream(target)) {

                file.mkdirs();
                byte[] buf = new byte[16384];
                int len;
                while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }


}
