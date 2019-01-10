package org.aksw.gerbil.qa.datatypes;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ProvenanceInfo;

import com.google.common.collect.Sets;

public class AnswerSet<T> implements Marking {

    protected Set<T> answers = Sets.newHashSet();

    public AnswerSet(Set<T> answers) {
        this.answers = answers;
    }

    public Set<T> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<T> answers) {
        this.answers = answers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((answers == null) ? 0 : answers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        AnswerSet<T> other = (AnswerSet<T>) obj;
        if (answers == null) {
            if (other.answers != null)
                return false;
        } else if (!answers.equals(other.answers))
            return false;
        return true;
    }

    @Override
    public Object clone() {
        return new AnswerSet<T>(answers);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AnswerSet [answers=");
        builder.append(answers.toString());
        builder.append("]");
        return builder.toString();
    }

	@Override
	public void setProvenanceInfo(ProvenanceInfo provencance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProvenanceInfo getProvenanceInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
