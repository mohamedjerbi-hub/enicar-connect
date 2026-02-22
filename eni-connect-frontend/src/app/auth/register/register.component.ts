import { Component } from '@angular/core';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent {
    selectedUserType: string = 'student';

    constructor() { }

    onSubmit() {
        // Registration logic will be implemented here
        console.log('Registration form submitted');
    }
}
