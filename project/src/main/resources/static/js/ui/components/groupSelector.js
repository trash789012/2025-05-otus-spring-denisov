export class GroupSelector {
    constructor(selector, groupId = 0) {
        this.groupSelector = document.getElementById(selector);
        if (!this.groupSelector) {
            throw new Error(`GroupSelector not found: ${selector}`);
        }
        this.groupId = groupId;
    }

    render(groups) {
        this.groupSelector.innerHTML = '';

        groups.forEach(group => {
            const option = document.createElement('option');
            option.value = group.id;
            option.textContent = group.name;
            option.selected = this.groupId === group.id;

            this.groupSelector.appendChild(option);
        });
    }

    getValue() {
        return this.groupSelector.value;
    }
}