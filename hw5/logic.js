"use strict"; //we will always add this according to lecture 10

function validForm(){
    const isValid = solution_js_q4();
    if(!isValid){
        alert("The form is invalid");
        return false;
    }else{
        alert("The form is valid");
        return true
    }
}

function solution_js_q4(){
    const usernameInput = document.getElementById("username").value; //get the username (by id) value from the html file
    const validChar = /^[a-zA-Z0-9\-]+$/; //we provide the user name from start to end to contain just a-z or A-Z or -
    if(usernameInput.length < 4 || !validChar.test(usernameInput)){
        return false;
    }

    const passwordInput = document.getElementById("password").value; //get the password (by id) value from the html file
    const aLOneSpecialChar = /[!@#$%^&*_()\-]/; //check if the string contain at list one of them !,@,#,$,%,^,&,*,_,(,),-
    const aLOneLetter = /[a-zA-Z]/; //check if the string contain at list one letter a-z or A-Z
    const aLOneNumber = /[0-9]/; //check if the string contain at list one number
    if(passwordInput.length < 8 || !aLOneSpecialChar.test(passwordInput) || !aLOneLetter.test(passwordInput) || !aLOneNumber.test(passwordInput) ){
        return false;
    }

    const emailInput = document.getElementById("email").value; //get the email (by id) value from the html file
    if(emailInput.includes("#")){ //if the email contain the char # invalid
        return false;
    }
    if(emailInput.includes("..")){ //if the email contain .. invalid
        return false;
    }
    if(emailInput.split("@").length != 2){ //if there is more then one @ invalid
        return false;
    }
    const [userPart,domainPart] = emailInput.split("@"); //split the email by using @ and call ech one of them by names: 1.user 2.domain
    if(userPart.startsWith(".") || userPart.startsWith("-") || userPart.endsWith(".") || userPart.endsWith("-") || userPart.length===0 ){
        return false;
    }
    if(!domainPart.includes(".")){
        return false;
    }
    const domainPartSplit = domainPart.split("."); //split the domainPart by .
    const getTLD_InDomain = domainPartSplit[domainPartSplit.length - 1] //get the Top-level string and save as getTLD_InDomain
    if (domainPartSplit.length < 2 || getTLD_InDomain.length < 2) {
        return false;
    }

    const ageInput = document.getElementById("age").value; //get the age (by id) value from the html file
    if(ageInput < 10 || ageInput > 120){ //check if this is a  reasonable age (between 10 and 120, inclusive).
        return false;
    }

    return true;
}

function solution_js_query(){
    const q5Div = document.querySelector("#q5"); //we will select the hole first append of div with id of q5 (witch mean all the links)
    const maliciousLinks = q5Div.querySelectorAll("a.malicious"); //select all the tags that the class name of them is malicious
    maliciousLinks.forEach(maliciousHelper); //send each one of them to maliciousHelper
    document.querySelector("div.hidden").setAttribute("style", "display:block") //select the hidden div in id of q5 and change his attribute to block
}

function maliciousHelper(element) {
    element.setAttribute("style", "display:none"); //make the malicious tags hidden
}

function solution_js_dynamic_elements(){
    const parentElement = document.getElementById("q6");
    const newDiv = document.createElement("div");
    const newH2 = document.createElement("h2");
    newH2.textContent = "this is my dynamic text h2";
    const newP = document.createElement("p");
    newP.textContent = "this is my dynamic text p";
    newDiv.appendChild(newH2);
    newDiv.appendChild(newP);
    parentElement.appendChild(newDiv);
}

function solution_js_event_listeners(){
    const divBtn = document.getElementById("div_btn"); //get the div
    divBtn.addEventListener("click" , () => { //add an event listener to it and an arrow func with alert
        alert("click");
    });

    document.body.addEventListener("keypress", event => { //add an event listener to the body of the doc and an arrow func that pop alert
        alert(`The key '${event.key}' was pressed`);
    });
}

function solution_js_unit_converter(){
    const inputNumber = parseFloat(document.getElementById("convertion_input").value); //read the string (because it is text type) from the convertion_input and make it float
    const fromUnits = document.getElementById("convert_from_unit").value;//get the units of the parameter that we want to convert
    const toUnits = document.getElementById("convert_to_unit").value;//get the units we want to convert to.
    const outputNumber = document.getElementById("convertion_output"); //will be useful later to write the answer
    const convert = new Convertor(); //make instance of Convertor class
    const result = convert.convert(inputNumber,fromUnits,toUnits); //send to the convert method in convert instance of Convertor our 3 arguments
    outputNumber.value = result; //insert to the value field our calc result
}

class Convertor {
    constructor(){ //make an constructor base on self study
        this.Convertor = { //access to the class and refer each unit as meter unit
            cm: 0.01,
            meter: 1,
            inch: 0.02540,
            foot: 0.3048
        }
    }
    convert(input,fromU,toU){ //build an calculation func from unit to meter and from meter to the require unit
        const valueInMeters = input*this.Convertor[fromU];
        const valueToReturn = valueInMeters/this.Convertor[toU];
        return valueToReturn;
    }
}


