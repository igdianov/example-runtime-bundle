/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.engine.impl.rules;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

public class RulesAgendaFilter implements AgendaFilter {

  protected List<String> rulesList = new ArrayList<String>();
  protected boolean accept;

  public RulesAgendaFilter() {
  }

  public boolean accept(Activation activation) {
    String ruleName = activation.getRule().getName();
    for (String rules : rulesList) {
      if (rules.contains(ruleName)) {
        return this.accept;
      }
    }
    return !this.accept;
  }

  public void addRules(String rules) {
    this.rulesList.add(rules);
  }

  public void setAccept(boolean accept) {
    this.accept = accept;
  }
}