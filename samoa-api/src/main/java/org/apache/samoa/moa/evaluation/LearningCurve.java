/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.samoa.moa.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.samoa.moa.AbstractMOAObject;
import org.apache.samoa.moa.core.DoubleVector;
import org.apache.samoa.moa.core.Measurement;
import org.apache.samoa.moa.core.StringUtils;
import org.apache.samoa.moa.core.Vote;

/**
 * Class that stores and keeps the history of evaluation measurements.
 * 
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class LearningCurve extends AbstractMOAObject {

  private static final long serialVersionUID = 1L;

  protected List<String> measurementNames = new ArrayList<>();

  protected List<String> voteNames = new ArrayList<>();

  protected List<double[]> measurementValues = new ArrayList<>();

  protected List<String> voteValues = new ArrayList<String>();

  public LearningCurve(String orderingMeasurementName) {
    this.measurementNames.add(orderingMeasurementName);
  }

  public String getOrderingMeasurementName() {
    return this.measurementNames.get(0);
  }

  public void insertEntry(LearningEvaluation learningEvaluation) {
    Measurement[] measurements = learningEvaluation.getMeasurements();
    Measurement orderMeasurement = Measurement.getMeasurementNamed(
        getOrderingMeasurementName(), measurements);
    if (orderMeasurement == null) {
      throw new IllegalArgumentException();
    }
    DoubleVector entryVals = new DoubleVector();
    for (Measurement measurement : measurements) {
      entryVals.setValue(addMeasurementName(measurement.getName()),
          measurement.getValue());
    }
    double orderVal = orderMeasurement.getValue();
    int index = 0;
    while ((index < this.measurementValues.size())
        && (orderVal > this.measurementValues.get(index)[0])) {
      index++;
    }
    this.measurementValues.add(index, entryVals.getArrayRef());
  }

  public int numEntries() {
    return this.measurementValues.size();
  }

  protected int addMeasurementName(String name) {
    int index = this.measurementNames.indexOf(name);
    if (index < 0) {
      index = this.measurementNames.size();
      this.measurementNames.add(name);
    }
    return index;
  }

  public String headerToString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String name : this.measurementNames) {
      if (!first) {
        sb.append(',');
      } else {
        first = false;
      }
      sb.append(name);
    }
    return sb.toString();
  }

  public String entryToString(int entryIndex) {
    StringBuilder sb = new StringBuilder();
    double[] vals = this.measurementValues.get(entryIndex);
    for (int i = 0; i < this.measurementNames.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      if ((i >= vals.length) || Double.isNaN(vals[i])) {
        sb.append('?');
      } else {
        sb.append(Double.toString(vals[i]));
      }
    }
    return sb.toString();
  }

  @Override
  public void getDescription(StringBuilder sb, int indent) {
    sb.append(headerToString());
    for (int i = 0; i < numEntries(); i++) {
      StringUtils.appendNewlineIndented(sb, indent, entryToString(i));
    }
  }

  public double getMeasurement(int entryIndex, int measurementIndex) {
    return this.measurementValues.get(entryIndex)[measurementIndex];
  }

  public String getMeasurementName(int measurementIndex) {
    return this.measurementNames.get(measurementIndex);
  }

  protected int addVoteName(String name) {
    int index = this.voteNames.indexOf(name);
    if (index < 0) {
      index = this.voteNames.size();
      this.voteNames.add(name);
    }
    return index;
  }

  public void setVote(Vote[] votes) {
    this.voteValues.clear();
    for (Vote vote : votes) {
      voteValues.add(addVoteName(vote.getName()), vote.getValue());
    }
  }

  /**
   * This method is used to set generate header line of a text file containing predictions and votes (for classification
   * only)
   *
   * @return String This returns the text of the header of a file containing predictions and votes.
   */
  public String voteHeaderToString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String name : this.voteNames) {
      if (!first) {
        sb.append(',');
      } else {
        first = false;
      }
      sb.append(name);
    }
    return sb.toString();
  }

  /**
   * This method is used to set generate one body line of a text file containing predictions and votes (for
   * classification only)
   *
   * @return String This returns the text of one line of a file containing predictions and votes.
   */
  public String voteEntryToString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.voteNames.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      if ((i >= voteValues.size())) {
        sb.append('?');
      } else {
        sb.append(voteValues.get(i));
      }
    }
    return sb.toString();
  }
}
