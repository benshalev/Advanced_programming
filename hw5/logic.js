
function solution_js_query(){
    const q5Element = document.querySelector("#q5");
    const maliciousLinks = q5Element.querySelectorAll("a.malicious");
    maliciousLinks.forEach(maliciousHelper);
    document.querySelector("div.hidden").setAttribute("style", "display:block")
}

function maliciousHelper(element) {
    element.setAttribute("style", "display:none");
}

function solution_js_ben(){
    const usernameInput = document.getElementById("username").value;
    const validChar = /^[a-zA-Z0-9\-]+$/;
    if(usernameInput.length < 4 || !validChar.test(usernameInput)){
        return false;
    }
    const passwordInput = document.getElementById("password").value;
    const specialChar = /[!@#$%^&*_()\-]/;
    const hasLetters = /[a-zA-Z]/;
    const hasNumber = /[0-9]/;
    if(passwordInput.length < 8 || !specialChar.test(passwordInput) || !hasLetters.test(passwordInput) || !hasNumber.test(passwordInput) ){
        return false;
    }

    const emailInput = document.getElementById("email").value;
    if(emailInput.includes("#")){
        return false;
    }
    if(emailInput.includes("..")){
        return false;
    }
    if(emailInput.split("@").length != 2){
        return false;
    }
    const [userPart,domainPart] = emailInput.split("@");
    if(userPart.startsWith(".") || userPart.startsWith("-") || userPart.endsWith(".") || userPart.endsWith("-") || userPart.length===0 ){
        return false;
    }

    if(!domainPart.includes(".")){
        return false;
    }

    const domainParts = domainPart.split(".");
    if (domainParts.length < 2 || domainParts[domainParts.length - 1].length < 2) {
        return false;
    }

    const ageInput = document.getElementById("age").value;
    if(ageInput<10 || ageInput>120){
        return false;
    }



    return true;
    }

function validForm(){
    const isValid = solution_js_ben();
    if(!isValid){
        alert("The form is invalid");
        return false;
    }else{
        alert("The form is valid");
        return true
    }
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
    
}

function solution_js_unit_converter(){
    
}

