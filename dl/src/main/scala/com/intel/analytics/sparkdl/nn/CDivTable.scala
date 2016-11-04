/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intel.analytics.sparkdl.nn

import com.intel.analytics.sparkdl.tensor.Tensor
import com.intel.analytics.sparkdl.tensor.TensorNumericMath.TensorNumeric
import com.intel.analytics.sparkdl.utils.Table

import scala.reflect.ClassTag


class CDivTable[T: ClassTag](implicit ev: TensorNumeric[T])
  extends Module[Table, Tensor[T], T]{

  override def updateOutput(input: Table): Tensor[T] = {
    val res1 = input[Tensor[T]](1)
    val res2 = input[Tensor[T]](2)

    output.resize(res1.size()).copy(res1)
    output.cdiv(res2)
    output
  }

  override def updateGradInput(input: Table, gradOutput: Tensor[T]): Table = {
    val res1 = input[Tensor[T]](1)
    val res2 = input[Tensor[T]](2)

    if (!gradInput.contains(1)) gradInput.insert(1, Tensor[T]())
    if (!gradInput.contains(2)) gradInput.insert(2, Tensor[T]())
    gradInput[Tensor[T]](1).resizeAs(res1).copy(gradOutput).cdiv(res2)
    gradInput[Tensor[T]](2).resizeAs(res2).zero().
      addcdiv(ev.fromType(-1), gradInput(1), res2).cmul(res1)

    gradInput
  }

  override def toString() : String = {
    "nn.CDivTable"
  }
}