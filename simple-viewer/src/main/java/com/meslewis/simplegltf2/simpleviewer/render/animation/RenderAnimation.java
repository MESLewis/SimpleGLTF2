/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render.animation;

import com.meslewis.simplegltf2.data.GLTFAnimation;
import com.meslewis.simplegltf2.data.GLTFAnimationSampler;
import com.meslewis.simplegltf2.data.GLTFChannel;
import com.meslewis.simplegltf2.simpleviewer.SimpleViewer;
import com.meslewis.simplegltf2.simpleviewer.render.RenderMesh;
import com.meslewis.simplegltf2.simpleviewer.render.RenderNode;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderAnimation {

  private static final Logger logger = LoggerFactory.getLogger(RenderAnimation.class);

  private final GLTFAnimation gltfAnimation;
  private final List<GLTFChannel> channels;
  private final List<GLTFAnimationSampler> samplers;
  //One interpolator per channel.
  private final List<Interpolator> interpolators = new ArrayList<>();


  public RenderAnimation(GLTFAnimation gltfAnimation,
      SimpleViewer simpleViewer) {
    this.gltfAnimation = gltfAnimation;
    this.channels = gltfAnimation.getChannels();
    this.samplers = gltfAnimation.getSamplers();

    for (GLTFChannel channel : channels) {
      if (channel.getTarget().getNode() != null) {
        interpolators.add(new Interpolator(channel, simpleViewer));
      }
    }
  }

  public void advance(float totalTime) {
    if (channels == null) {
      return;
    }

    for (Interpolator interpolator : interpolators) {
      GLTFChannel channel = interpolator.getChannel();
      GLTFAnimationSampler sampler = samplers.get(channel.getAnimationSamplerIndex());
      RenderNode node = interpolator.getRenderNode();

      switch (channel.getTarget().getPath()) {
        case TRANSLATION:
          interpolator.interpolate(totalTime, sampler, node.getTranslation());
          break;
        case ROTATION:
          interpolator.interpolate(totalTime, sampler, node.getRotation());
          break;
        case SCALE:
          interpolator.interpolate(totalTime, sampler, node.getScale());
          break;
        case WEIGHTS:
          if (node instanceof RenderMesh) {
            RenderMesh mesh = (RenderMesh) node;
            interpolator.interpolate(totalTime, sampler, mesh.getWeights());
          } else {
            logger.error("Error weights must be applied to RenderMesh");
          }
          break;

      }
    }
  }
}
